package com.online.estoreshop.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.annotations.NotNull;
import com.online.estoreshop.R;
import com.online.estoreshop.interfaces.ClickedItem;
import com.online.estoreshop.models.CategoryDomain;
import com.online.estoreshop.models.PackRequest;

import java.util.ArrayList;
import java.util.List;

public class AddPackFragment extends BottomSheetDialogFragment {

    String msg;
    SelectCategoryAdapter adapter;
    ArrayList<CategoryDomain> productList = new ArrayList<>();

    public AddPackFragment() {
        // Required empty public constructor
    }


    OptionListener2 optionListener;

    public void setOptionListener2(OptionListener2 optionListener) {
        this.optionListener = optionListener;
    }

    public interface OptionListener2 {
        void optionSelected2(PackRequest packRequest);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NotNull final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.fragment_add_pack, null);
        dialog.setContentView(contentView);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
//        CoordinatorLayout.Behavior behavior = params.getBehavior();
//
//        if (behavior instanceof BottomSheetBehavior) {
//            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
//        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        final EditText edtQty = contentView.findViewById(R.id.edtQty);
        final EditText edtPrice = contentView.findViewById(R.id.edtPrice);
        final EditText edtspecialPrize = contentView.findViewById(R.id.edtspecialPrize);
        final EditText edtDiscount = contentView.findViewById(R.id.edtDiscount);
        TextView btnSubmit = contentView.findViewById(R.id.btnSubmit);
        final Spinner edtUnit = contentView.findViewById(R.id.edtUnit);

        List<String> categories = new ArrayList<String>();
        categories.add("no.s");
        categories.add("kg");
        categories.add("g");
        categories.add("mg");
        categories.add("m");
        categories.add("ml");
        categories.add("l");
        categories.add("V");
        categories.add("cm");
        categories.add("inch");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtUnit.setAdapter(dataAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String qty = edtQty.getText().toString();
                String unit = edtUnit.getSelectedItem().toString();
                String proce = edtPrice.getText().toString();
                String sprice = edtspecialPrize.getText().toString();
                String discount = edtDiscount.getText().toString();
                if (!qty.equals("") && !proce.equals("") && !sprice.equals("") && !discount.equals("")) {
                    PackRequest packRequest = new PackRequest("", unit, qty, "", proce, sprice, discount);
                    optionListener.optionSelected2(packRequest);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Enter Values", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

    }


}
