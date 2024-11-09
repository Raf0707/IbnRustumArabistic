package ibn.rustum.arabistic.ui.words.arabic_for_russian.cards.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ibn.rustum.arabistic.R
import ibn.rustum.arabistic.databinding.FragmentTestModeBinding

class TestModeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTestModeBinding.inflate(inflater, container, false)

        val checkedArabicWords = arguments?.getStringArray("checkedArabicWords") ?: arrayOf()
        val checkedRussianWords = arguments?.getStringArray("checkedRussianWords") ?: arrayOf()

        // Используйте checkedArabicWords и checkedRussianWords для тестирования

        return binding.root
    }
}