package dev.weg.alfa.security.bootstrap

import dev.weg.alfa.security.models.role.Permission
import dev.weg.alfa.security.models.role.Role
import dev.weg.alfa.security.repositories.RoleRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class RoleBootstrap(
    private val roleRepository: RoleRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        createAdminRoleIfMissing()
        createReadOnlyRoleIfMissing()
    }

    private fun createAdminRoleIfMissing() {
        val name = "ADMIN"
        val allPermissions = Permission.entries.toSet()

        val role = roleRepository.findByName(name)

        if (role == null) {
            roleRepository.save(
                Role(
                    name = name,
                    permissions = allPermissions,
                )
            )
        } else {
            if (role.permissions != allPermissions) {
                val updated = role.copy(permissions = allPermissions)
                roleRepository.save(updated)
            }
        }
    }

    private fun createReadOnlyRoleIfMissing() {
        val name = "READ_ONLY"
        val readOnlyPermissions = Permission.entries.filter {
            it.name.startsWith("VIEW_")
        }.toSet()

        val role = roleRepository.findByName(name)

        if (role == null) {
            roleRepository.save(
                Role(
                    name = name,
                    permissions = readOnlyPermissions,
                )
            )
        } else {
            if (role.permissions != readOnlyPermissions) {
                val updated = role.copy(permissions = readOnlyPermissions)
                roleRepository.save(updated)
            }
        }
    }
}
