package com.example.homemaker.Helpers

class LaggedVariable {
    private var activatedAt = Long.MAX_VALUE
    fun activate() {
        activatedAt = System.currentTimeMillis()
    }

    val isActive: Boolean
        get() {
            val activeFor = System.currentTimeMillis() - activatedAt
            return activeFor >= 0 && activeFor <= DURATION
        }

    companion object {
        private const val DURATION = 5000
    }
}