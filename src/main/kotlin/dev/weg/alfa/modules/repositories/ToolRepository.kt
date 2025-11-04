package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.tool.Tool
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ToolRepository : JpaRepository <Tool, Int>
