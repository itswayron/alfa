package dev.weg.alfa.modules.repositories

import BaseTest
import dev.weg.alfa.modules.models.stock.Stock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:movrepo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.show-sql=false",
        "spring.flyway.enabled=false"
    ]
)
class MovementRepositoryTest : BaseTest() {

    @Autowired lateinit var jdbc: JdbcTemplate
    @Autowired lateinit var movementRepository: MovementRepository
    @Autowired lateinit var stockRepository: StockRepository

    @BeforeEach
    fun setupSchema() {
        jdbc.execute("DROP TABLE IF EXISTS movement")
        jdbc.execute("DROP TABLE IF EXISTS movement_batch")
        jdbc.execute("DROP TABLE IF EXISTS business_partner")
        jdbc.execute("DROP TABLE IF EXISTS stock")
        jdbc.execute("DROP TABLE IF EXISTS item")
        jdbc.execute("DROP TABLE IF EXISTS position")
        jdbc.execute("DROP TABLE IF EXISTS employee")
        jdbc.execute("DROP TABLE IF EXISTS sector")
        jdbc.execute("DROP TABLE IF EXISTS movement_type")
        jdbc.execute("DROP TABLE IF EXISTS movement_status")
        jdbc.execute("DROP TABLE IF EXISTS measurement_unity")
        jdbc.execute("DROP TABLE IF EXISTS subgroup")
        jdbc.execute("DROP TABLE IF EXISTS groups")

        jdbc.execute(
            """
            CREATE TABLE groups (
                id INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE subgroup (
                id INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE measurement_unity (
                id INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE movement_status (
                id INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE movement_type (
                id INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                affects_average_price BOOLEAN NOT NULL,
                quantity_sign INT
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE sector (
                id INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE employee (
                id INT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                sector_id INT NOT NULL,
                CONSTRAINT fk_employee_sector FOREIGN KEY (sector_id) REFERENCES sector(id)
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE position (
                id INT PRIMARY KEY,
                floor VARCHAR(50),
                side VARCHAR(50),
                "column" VARCHAR(50),
                box VARCHAR(50)
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE item (
                id INT PRIMARY KEY,
                code VARCHAR(255) NOT NULL UNIQUE,
                description TEXT NOT NULL,
                group_id INT NOT NULL,
                subgroup_id INT NOT NULL,
                dimensions VARCHAR(100),
                material VARCHAR(100),
                is_active BOOLEAN NOT NULL DEFAULT TRUE,
                image_path VARCHAR(255),
                measurement_unity_id INT NOT NULL,
                main_supplier_id INT,
                CONSTRAINT fk_item_group FOREIGN KEY (group_id) REFERENCES groups(id),
                CONSTRAINT fk_item_subgroup FOREIGN KEY (subgroup_id) REFERENCES subgroup(id),
                CONSTRAINT fk_item_unit FOREIGN KEY (measurement_unity_id) REFERENCES measurement_unity(id)
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE business_partner (
                id INT PRIMARY KEY,
                cnpj VARCHAR(20),
                "name" VARCHAR(255) NOT NULL,
                relation VARCHAR(50)
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE stock (
                id INT PRIMARY KEY,
                item_id INT NOT NULL,
                current_amount DOUBLE PRECISION NOT NULL,
                minimum_amount DOUBLE PRECISION,
                maximum_amount DOUBLE PRECISION,
                average_price DOUBLE PRECISION NOT NULL,
                sector_id INT NOT NULL,
                position_id INT NOT NULL,
                CONSTRAINT fk_stock_item FOREIGN KEY (item_id) REFERENCES item(id),
                CONSTRAINT fk_stock_sector FOREIGN KEY (sector_id) REFERENCES sector(id),
                CONSTRAINT fk_stock_position FOREIGN KEY (position_id) REFERENCES position(id)
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE movement_batch (
                id INT PRIMARY KEY,
                code VARCHAR(255) NOT NULL UNIQUE,
                document VARCHAR(255),
                date TIMESTAMP NOT NULL,
                business_partner_id INT,
                observation TEXT
            )
            """.trimIndent()
        )
        jdbc.execute(
            """
            CREATE TABLE movement (
                id INT PRIMARY KEY,
                stock_id INT NOT NULL,
                production_order_id INT,
                quantity DOUBLE PRECISION NOT NULL,
                price DOUBLE PRECISION,
                type_id INT NOT NULL,
                date TIMESTAMP NOT NULL,
                employee_id INT NOT NULL,
                observation TEXT,
                status_id INT NOT NULL,
                sector_id INT NOT NULL,
                CONSTRAINT fk_movement_stock FOREIGN KEY (stock_id) REFERENCES stock(id),
                CONSTRAINT fk_movement_batch FOREIGN KEY (production_order_id) REFERENCES movement_batch(id),
                CONSTRAINT fk_movement_type FOREIGN KEY (type_id) REFERENCES movement_type(id),
                CONSTRAINT fk_movement_employee FOREIGN KEY (employee_id) REFERENCES employee(id),
                CONSTRAINT fk_movement_status FOREIGN KEY (status_id) REFERENCES movement_status(id),
                CONSTRAINT fk_movement_sector FOREIGN KEY (sector_id) REFERENCES sector(id)
            )
            """.trimIndent()
        )
    }

    @Test
    fun `should find all movements by stockId`() {
        jdbc.execute("INSERT INTO groups (id, name) VALUES (1, 'Group 1')")
        jdbc.execute("INSERT INTO subgroup (id, name) VALUES (1, 'Subgroup 1')")
        jdbc.execute("INSERT INTO measurement_unity (id, name) VALUES (1, 'Meter')")
        jdbc.execute("INSERT INTO movement_status (id, name) VALUES (1, 'ACTIVE')")
        jdbc.execute("INSERT INTO movement_type (id, name, affects_average_price, quantity_sign) VALUES (1, 'ENTRY', true, 1)")
        jdbc.execute("INSERT INTO sector (id, name) VALUES (1, 'Sector 1')")
        jdbc.execute("INSERT INTO employee (id, name, sector_id) VALUES (1, 'Employee 1', 1)")
        jdbc.execute("INSERT INTO position (id, floor, side, \"column\", box) VALUES (1, '1', 'A', '1', '1')")
        jdbc.execute("INSERT INTO item (id, code, description, group_id, subgroup_id, dimensions, material, is_active, image_path, measurement_unity_id, main_supplier_id) VALUES (1, 'Item 1', 'Item Description 1', 1, 1, 'Dimensions', 'Material', true, 'image', 1, 1)")
        jdbc.execute("INSERT INTO item (id, code, description, group_id, subgroup_id, dimensions, material, is_active, image_path, measurement_unity_id, main_supplier_id) VALUES (2, 'Item 2', 'Item Description 2', 1, 1, 'Dimensions', 'Material', true, 'image', 1, 1)")
        jdbc.execute("INSERT INTO item (id, code, description, group_id, subgroup_id, dimensions, material, is_active, image_path, measurement_unity_id, main_supplier_id) VALUES (3, 'Item 3', 'Item Description 3', 1, 1, 'Dimensions', 'Material', true, 'image', 1, 1)")
        jdbc.execute("INSERT INTO stock (id, item_id, current_amount, minimum_amount, maximum_amount, average_price, sector_id, position_id) VALUES (1, 1, 10.0, 5.0, 20.0, 5.0, 1, 1)")
        jdbc.execute("INSERT INTO stock (id, item_id, current_amount, minimum_amount, maximum_amount, average_price, sector_id, position_id) VALUES (2, 2, 10.0, 5.0, 20.0, 5.0, 1, 1)")
        jdbc.execute("INSERT INTO stock (id, item_id, current_amount, minimum_amount, maximum_amount, average_price, sector_id, position_id) VALUES (3, 3, 10.0, 5.0, 20.0, 5.0, 1, 1)")
        jdbc.execute("INSERT INTO movement_batch (id, code, document, date, business_partner_id, observation) VALUES (1, '123', 'doc', '2024-01-01', 1, 'note')")
        jdbc.execute("INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id) VALUES (1, 1, 1, 10.0, 5.0, 1, '2024-01-01', 1, 'note', 1, 1)")
        jdbc.execute("INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id) VALUES (2, 1, 1, 10.0, 5.0, 1, '2024-01-01', 1, 'note', 1, 1)")
        jdbc.execute("INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id) VALUES (3, 1, 1, 10.0, 5.0, 1, '2024-01-01', 1, 'note', 1, 1)")
        jdbc.execute("INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id) VALUES (4, 2, 1, 10.0, 5.0, 1, '2024-01-01', 1, 'note', 1, 1)")
        jdbc.execute("INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id) VALUES (5, 3, 1, 10.0, 5.0, 1, '2024-01-01', 1, 'note', 1, 1)")

        val movementsList = movementRepository.findAllByStockId(1)

        assertEquals(3, movementsList.size)
    }

    @Test
    fun `findLastMovementsAffectingAveragePrice should filter by stock, affectsAveragePrice and status CONCLUIDO, order desc and apply pagination`() {
        jdbc.update("INSERT INTO groups (id, name) VALUES (1, 'G')")
        jdbc.update("INSERT INTO subgroup (id, name) VALUES (1, 'SG')")
        jdbc.update("INSERT INTO measurement_unity (id, name) VALUES (1, 'UNI')")

        jdbc.update("INSERT INTO movement_status (id, name) VALUES (1, 'CONCLUIDO')")
        jdbc.update("INSERT INTO movement_status (id, name) VALUES (2, 'CANCELADO')")

        jdbc.update("INSERT INTO movement_type (id, name, affects_average_price, quantity_sign) VALUES (1, 'ENTRADA', TRUE, 1)")
        jdbc.update("INSERT INTO movement_type (id, name, affects_average_price, quantity_sign) VALUES (2, 'SAIDA', FALSE, -1)")

        jdbc.update("INSERT INTO sector (id, name) VALUES (1, 'ALMOX')")
        jdbc.update("INSERT INTO employee (id, name, sector_id) VALUES (1, 'EMP', 1)")
        jdbc.update("""INSERT INTO position (id, floor, side, "column", box) VALUES (1, '1', 'L', '1', '1')""")

        jdbc.update(
            """
            INSERT INTO item (id, code, description, group_id, subgroup_id, dimensions, material, is_active, image_path, measurement_unity_id, main_supplier_id)
            VALUES (1, 'I1', 'Item 1', 1, 1, NULL, NULL, TRUE, NULL, 1, NULL)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO item (id, code, description, group_id, subgroup_id, dimensions, material, is_active, image_path, measurement_unity_id, main_supplier_id)
            VALUES (2, 'I2', 'Item 2', 1, 1, NULL, NULL, TRUE, NULL, 1, NULL)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO stock (id, item_id, current_amount, minimum_amount, maximum_amount, average_price, sector_id, position_id)
            VALUES (100, 1, 0, NULL, NULL, 0, 1, 1)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO stock (id, item_id, current_amount, minimum_amount, maximum_amount, average_price, sector_id, position_id)
            VALUES (200, 2, 0, NULL, NULL, 0, 1, 1)
            """.trimIndent()
        )

        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (1, 100, NULL, 1.0, 10.0, 1, TIMESTAMP '2025-01-01 10:00:00', 1, NULL, 1, 1)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (2, 100, NULL, 1.0, 11.0, 1, TIMESTAMP '2025-01-02 10:00:00', 1, NULL, 1, 1)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (3, 100, NULL, 1.0, 12.0, 2, TIMESTAMP '2025-01-03 10:00:00', 1, NULL, 1, 1)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (4, 100, NULL, 1.0, 13.0, 1, TIMESTAMP '2025-01-04 10:00:00', 1, NULL, 2, 1)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (5, 200, NULL, 1.0, 14.0, 1, TIMESTAMP '2025-01-05 10:00:00', 1, NULL, 1, 1)
            """.trimIndent()
        )

        val stock: Stock = stockRepository.findById(100).orElseThrow()

        val page0size1 = PageRequest.of(0, 1)
        val resultPage0 = movementRepository.findLastMovementsAffectingAveragePrice(stock, page0size1)
        assertEquals(1, resultPage0.size)
        assertEquals(2, resultPage0.first().id, "Expected latest valid movement (by date desc)")

        val page1size1 = PageRequest.of(1, 1)
        val resultPage1 = movementRepository.findLastMovementsAffectingAveragePrice(stock, page1size1)
        assertEquals(1, resultPage1.size)
        assertEquals(1, resultPage1.first().id, "Expected second latest valid movement")

        val page0size10 = PageRequest.of(0, 10)
        val all = movementRepository.findLastMovementsAffectingAveragePrice(stock, page0size10)
        assertEquals(listOf(2, 1), all.map { it.id })
    }

    @Test
    fun `findAllByMovementBatchId and countByMovementBatchId should match only given batch`() {
        jdbc.update("INSERT INTO groups (id, name) VALUES (1, 'G')")
        jdbc.update("INSERT INTO subgroup (id, name) VALUES (1, 'SG')")
        jdbc.update("INSERT INTO measurement_unity (id, name) VALUES (1, 'UNI')")
        jdbc.update("INSERT INTO movement_status (id, name) VALUES (1, 'CONCLUIDO')")
        jdbc.update("INSERT INTO movement_type (id, name, affects_average_price, quantity_sign) VALUES (1, 'ENTRADA', TRUE, 1)")
        jdbc.update("INSERT INTO sector (id, name) VALUES (1, 'ALMOX')")
        jdbc.update("INSERT INTO employee (id, name, sector_id) VALUES (1, 'EMP', 1)")
        jdbc.update("""INSERT INTO position (id, floor, side, "column", box) VALUES (1, '1', 'L', '1', '1')""")
        jdbc.update(
            """
            INSERT INTO item (id, code, description, group_id, subgroup_id, dimensions, material, is_active, image_path, measurement_unity_id, main_supplier_id)
            VALUES (1, 'I1', 'Item 1', 1, 1, NULL, NULL, TRUE, NULL, 1, NULL)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO stock (id, item_id, current_amount, minimum_amount, maximum_amount, average_price, sector_id, position_id)
            VALUES (100, 1, 0, NULL, NULL, 0, 1, 1)
            """.trimIndent()
        )

        jdbc.update(
            """
            INSERT INTO movement_batch (id, code, document, date, business_partner_id, observation)
            VALUES (10, 'MB10', NULL, TIMESTAMP '2025-01-01 00:00:00', NULL, NULL)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO movement_batch (id, code, document, date, business_partner_id, observation)
            VALUES (20, 'MB20', NULL, TIMESTAMP '2025-01-01 00:00:00', NULL, NULL)
            """.trimIndent()
        )

        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (1, 100, 10, 1.0, 10.0, 1, TIMESTAMP '2025-01-01 10:00:00', 1, NULL, 1, 1)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (2, 100, 10, 1.0, 11.0, 1, TIMESTAMP '2025-01-02 10:00:00', 1, NULL, 1, 1)
            """.trimIndent()
        )
        jdbc.update(
            """
            INSERT INTO movement (id, stock_id, production_order_id, quantity, price, type_id, date, employee_id, observation, status_id, sector_id)
            VALUES (3, 100, 20, 1.0, 12.0, 1, TIMESTAMP '2025-01-03 10:00:00', 1, NULL, 1, 1)
            """.trimIndent()
        )

        val list10 = movementRepository.findAllByMovementBatchId(10)
        assertEquals(2, list10.size)
        assertTrue(list10.all { it.movementBatch?.id == 10 })

        val count10 = movementRepository.countByMovementBatchId(10)
        assertEquals(2, count10)

        val list20 = movementRepository.findAllByMovementBatchId(20)
        assertEquals(1, list20.size)
        assertTrue(list20.all { it.movementBatch?.id == 20 })

        val count20 = movementRepository.countByMovementBatchId(20)
        assertEquals(1, count20)
    }
}
