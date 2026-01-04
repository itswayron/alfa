package dev.weg.alfa.modules.services

import BaseTest
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.services.ImageService
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.item.Item
import dev.weg.alfa.modules.models.item.ItemPatch
import dev.weg.alfa.modules.models.item.ItemRequest
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.repositories.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.ItemRepository
import dev.weg.alfa.modules.repositories.simpleEntities.GroupRepository
import dev.weg.alfa.modules.repositories.simpleEntities.MeasurementUnityRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import dev.weg.alfa.modules.repositories.utils.FakeJpaRepository
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class ItemServiceTest : BaseTest() {

    private class FakeItemRepository(
        findByIdAnswer: (Int) -> Optional<Item>,
        private val saveAnswer: (Item) -> Item = { it },
        private val deleteByIdAnswer: (Int) -> Unit = {},
        private val findAllAnswer: () -> List<Item> = { emptyList() }
    ) : FakeJpaRepository<Item, Int>(findByIdAnswer), ItemRepository {
        override fun <S : Item> save(entity: S): S {
            @Suppress("UNCHECKED_CAST")
            return saveAnswer(entity) as S
        }

        override fun deleteById(id: Int) {
            deleteByIdAnswer(id)
        }

        override fun findAll(): MutableList<Item> = findAllAnswer().toMutableList()
    }

    private class FakeGroupRepository(
        findByIdAnswer: (Int) -> Optional<Group>
    ) : FakeJpaRepository<Group, Int>(findByIdAnswer), GroupRepository

    private class FakeSubgroupRepository(
        findByIdAnswer: (Int) -> Optional<Subgroup>
    ) : FakeJpaRepository<Subgroup, Int>(findByIdAnswer), SubgroupRepository

    private class FakeUnitRepository(
        findByIdAnswer: (Int) -> Optional<MeasurementUnity>
    ) : FakeJpaRepository<MeasurementUnity, Int>(findByIdAnswer), MeasurementUnityRepository

    private class FakePartnerRepository(
        findByIdAnswer: (Int) -> Optional<BusinessPartner>
    ) : FakeJpaRepository<BusinessPartner, Int>(findByIdAnswer), BusinessPartnerRepository

    @AfterEach
    fun clearAuditContext() {
        AuditContext.consume()
    }

    @Test
    fun `createItem should sanitize ItemRequest fields on create`() {
        val group = Group(id = 1, name = "G")
        val subgroup = Subgroup(id = 1, name = "S")
        val unit = MeasurementUnity(id = 1, name = "U")

        val itemSavedCaptor = mutableListOf<Item>()

        val repo = spyk(
            FakeItemRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { it.copy(id = 42).also { saved -> itemSavedCaptor.add(saved) } }
            )
        )

        val groupRepo = spyk(FakeGroupRepository { Optional.of(group) })
        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.of(subgroup) })
        val unitRepo = spyk(FakeUnitRepository { Optional.of(unit) })
        val partnerRepo = spyk(FakePartnerRepository { Optional.empty() })

        val imageService = spyk(ImageService({}) )

        val service = ItemService(
            repository = repo,
            groupRepository = groupRepo,
            subgroupRepository = subgroupRepo,
            unitRepository = unitRepo,
            partnerRepository = partnerRepo,
            imageService = imageService
        )

        val request = ItemRequest(
            code = "  CODE  ",
            description = "  Desc  ",
            groupId = 1,
            subgroupId = 1,
            dimensions = "  dim  ",
            material = "  mat  ",
            measurementUnityId = 1,
            mainSupplierId = null
        )

        val created = service.createItem(request)

        assertEquals(42, created.id)
        assertEquals("CODE", created.code)
        assertEquals("Desc", created.description)
        assertEquals("dim", created.dimensions)
        assertEquals("mat", created.material)

        assertEquals(1, itemSavedCaptor.size)
        val saved = itemSavedCaptor.first()
        assertEquals("CODE", saved.code)
        assertEquals("Desc", saved.description)
        assertEquals("dim", saved.dimensions)
        assertEquals("mat", saved.material)

        verify(exactly = 1) { repo.save(any<Item>()) }
    }

    @Test
    fun `updateItem should apply patch preserving unspecified fields`() {
        val oldItem = Item(
            id = 10,
            code = "C1",
            description = "Old Desc",
            group = Group(id = 1, name = "G"),
            subgroup = Subgroup(id = 1, name = "S"),
            dimensions = "D1",
            material = "M1",
            isActive = true,
            imagePath = null,
            measurementUnity = MeasurementUnity(id = 1, name = "U"),
            mainSupplier = null
        )

        var savedArg: Item? = null
        val repo = spyk(
            FakeItemRepository(
                findByIdAnswer = { id -> if (id == 10) Optional.of(oldItem) else Optional.empty() },
                saveAnswer = { entity -> savedArg = entity; entity }
            )
        )

        val groupRepo = spyk(FakeGroupRepository { Optional.empty() })
        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val unitRepo = spyk(FakeUnitRepository { Optional.empty() })
        val partnerRepo = spyk(FakePartnerRepository { Optional.empty() })
        val imageService = spyk(ImageService({}))

        val service = ItemService(
            repository = repo,
            groupRepository = groupRepo,
            subgroupRepository = subgroupRepo,
            unitRepository = unitRepo,
            partnerRepository = partnerRepo,
            imageService = imageService
        )

        val patch = ItemPatch(
            code = null,
            description = "New Desc",
            groupId = null,
            subgroupId = null,
            dimensions = null,
            material = null,
            measurementUnityId = null,
            mainSupplierId = null,
            imagePath = null,
            isActive = null
        )

        val updated = service.updateItem(10 to patch)

        assertEquals(10, updated.id)
        assertEquals("C1", updated.code)
        assertEquals("New Desc", updated.description)
        assertEquals("D1", updated.dimensions)
        assertEquals("M1", updated.material)

        assertNotNull(savedArg)
        assertEquals(10, savedArg!!.id)
        assertEquals("New Desc", savedArg!!.description)

        verify(exactly = 1) { repo.findById(10) }
        verify(exactly = 1) { repo.save(any<Item>()) }
    }

    @Test
    fun `deleteItem should delete item with existing image and trigger image deletion`() {
        val itemWithImage = Item(
            id = 5,
            code = "C5",
            description = "Has Image",
            group = Group(id = 1, name = "G"),
            subgroup = Subgroup(id = 1, name = "S"),
            dimensions = null,
            material = null,
            isActive = true,
            imagePath = "/images/items/existing.jpg",
            measurementUnity = MeasurementUnity(id = 1, name = "U"),
            mainSupplier = null
        )

        var deletedId: Int? = null
        val repo = spyk(
            FakeItemRepository(
                findByIdAnswer = { id -> if (id == 5) Optional.of(itemWithImage) else Optional.empty() },
                saveAnswer = { it },
                deleteByIdAnswer = { id -> deletedId = id }
            )
        )

        val imageService = spyk(object : ImageService({}) {})

        val groupRepo = spyk(FakeGroupRepository { Optional.empty() })
        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val unitRepo = spyk(FakeUnitRepository { Optional.empty() })
        val partnerRepo = spyk(FakePartnerRepository { Optional.empty() })

        val service = ItemService(
            repository = repo,
            groupRepository = groupRepo,
            subgroupRepository = subgroupRepo,
            unitRepository = unitRepo,
            partnerRepository = partnerRepo,
            imageService = imageService
        )

        service.deleteItem(5)

        assertEquals(5, deletedId)
        verify(exactly = 1) { imageService.deleteImage("/images/items/existing.jpg") }
        verify(exactly = 1) { repo.deleteById(5) }
    }

    @Test
    fun `uploadItemImage should delete previous image and save new image path`() {
        val itemWithImage = Item(
            id = 7,
            code = "C7",
            description = "Replace Image",
            group = Group(id = 1, name = "G"),
            subgroup = Subgroup(id = 1, name = "S"),
            dimensions = null,
            material = null,
            isActive = true,
            imagePath = "/images/items/old.jpg",
            measurementUnity = MeasurementUnity(id = 1, name = "U"),
            mainSupplier = null
        )

        val savedItems = mutableListOf<Item>()
        val repo = spyk(
            FakeItemRepository(
                findByIdAnswer = { id -> if (id == 7) Optional.of(itemWithImage) else Optional.empty() },
                saveAnswer = { savedItems.add(it); it }
            )
        )

        val imageService = spyk(object : ImageService({}) {
            override fun saveImage(entityType: EntityType, entityId: String, file: org.springframework.web.multipart.MultipartFile): String {
                return "/images/items/new.jpg"
            }
        })

        val groupRepo = spyk(FakeGroupRepository { Optional.empty() })
        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val unitRepo = spyk(FakeUnitRepository { Optional.empty() })
        val partnerRepo = spyk(FakePartnerRepository { Optional.empty() })

        val service = ItemService(
            repository = repo,
            groupRepository = groupRepo,
            subgroupRepository = subgroupRepo,
            unitRepository = unitRepo,
            partnerRepository = partnerRepo,
            imageService = imageService
        )

        val file = object : org.springframework.web.multipart.MultipartFile {
            override fun getName(): String = "file"
            override fun getOriginalFilename(): String = "f.jpg"
            override fun getContentType(): String = "image/jpeg"
            override fun isEmpty(): Boolean = false
            override fun getSize(): Long = 1
            override fun getBytes(): ByteArray = byteArrayOf(0)
            override fun getInputStream() = java.io.ByteArrayInputStream(byteArrayOf(0))
            override fun transferTo(dest: java.io.File) {}
        }

        service.uploadItemImage(7, file)

        verify(exactly = 1) { imageService.deleteImage("/images/items/old.jpg") }
        verify(exactly = 1) { imageService.saveImage(ImageService.EntityType.ITEM, "7", file) }

        assertTrue(savedItems.any { it.imagePath == "/images/items/new.jpg" })
    }
}
