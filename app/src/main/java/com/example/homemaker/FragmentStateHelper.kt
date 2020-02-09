package com.example.homemaker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Helps save and restore Fragment state outside of an Activity's typical
 * save/restore state lifecycle.
 *
 * A more complete implementation would also offer a mechanism for saving/restoring
 * the state of this helper (that is, all the Fragment states it has saved) that can
 * be used in conjunction with the Activity's save/restore cycle.
 */
class FragmentStateHelper(val fragmentManager: FragmentManager) {

    private val fragmentSavedStates = mutableMapOf<String, Fragment.SavedState?>()

    /**
     * Restore a Fragment's previously saved state via [saveState]
     *
     * @param fragment The Fragment whose state should be restored
     * @param key A key that uniquely identifies this Fragment
     */
    fun restoreState(fragment: Fragment, key: String) {
        fragmentSavedStates[key]?.let { savedState ->
            // We can't set the initial saved state if the Fragment is already added
            // to a FragmentManager, since it would then already be created.
            if (!fragment.isAdded) {
                fragment.setInitialSavedState(savedState)
            }
        }
    }

    /**
     * Save a Fragment's state for later restoration via [restoreState]
     *
     * @param fragment The Fragment whose state should be restored
     * @param key A key that uniquely identifies this Fragment
     */
    fun saveState(fragment: Fragment, key: String) {
        // We can't save the state of a Fragment that isn't added to a FragmentManager.
        if (fragment.isAdded ?: false) {
            fragmentSavedStates[key] = fragmentManager.saveFragmentInstanceState(fragment)
        }
    }
}