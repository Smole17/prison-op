package ru.starfarm.mode.service.player.`object`.group

class GroupScope(val type: Groups, val expireIn: Long) {

    override fun toString(): String = type.toString()
}