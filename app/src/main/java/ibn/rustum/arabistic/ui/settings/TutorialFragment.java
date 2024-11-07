package ibn.rustum.arabistic.ui.settings;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import ibn.rustum.arabistic.databinding.FragmentTutorialBinding;


public class TutorialFragment extends Fragment {
    FragmentTutorialBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTutorialBinding.inflate(getLayoutInflater());

        binding.openCounterListBtn.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Список счетчиков", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.changeCounterModeBtn.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "смена режима счетчика", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.deleteDBCounterItem.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "удалить счетчик", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.editCounterBtn.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "изменить счетчик", Snackbar.LENGTH_SHORT).setAction(
                            "понятно", view -> Toast.makeText(getContext(),
                                    "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.openCounterListBtn.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Список счетчиков",
                            Snackbar.LENGTH_SHORT).setAction(
                                    "понятно", view -> Toast.makeText(getContext(),
                                    "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.saveCounterEditions.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Сохранить изменения", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.openTutorialBtn.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "обучение по кнопкам", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.openSettingsBtn.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Настройки счетчика", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.counterResetBtn.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "обновить счетчик", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.counterBtnPlus.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "прибавить значение к счетчику", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        binding.counterBtnMinus.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(),
                    "Вычесть значение из счетчика", Snackbar.LENGTH_SHORT)
                    .setAction("понятно", view -> Toast.makeText(getContext(),
                            "Я рад за вас", Toast.LENGTH_SHORT).show())
                    .show();
        });

        return binding.getRoot();
    }
}