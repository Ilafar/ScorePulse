package com.score.pulse.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

/** The 12 stylized player emblems offered by the roster/add-player picker. */
enum class PlayerEmblem(val icon: ImageVector, val label: String) {
    Gamepad(Icons.Filled.SportsEsports, "Gamepad"),
    Thunder(Icons.Filled.Bolt, "Thunder"),
    Phoenix(Icons.Filled.LocalFireDepartment, "Phoenix"),
    Mecha(Icons.Filled.SmartToy, "Mecha"),
    Skull(Icons.Filled.Dangerous, "Skull"),
    Shield(Icons.Filled.Shield, "Shield"),
    Arcade(Icons.Filled.Star, "Arcade"),
    Sniper(Icons.Filled.GpsFixed, "Sniper"),
    Stealth(Icons.Filled.VisibilityOff, "Stealth"),
    Saber(Icons.Filled.ContentCut, "Saber"),
    Strike(Icons.Filled.FitnessCenter, "Strike"),
    Apex(Icons.Filled.Pets, "Apex"),
}
