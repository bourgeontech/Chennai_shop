package com.online.estoreshop.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import java.util.ArrayList;

public class CategoryFragment extends BottomSheetDialogFragment implements ClickedItem {

    String msg;
    SelectCategoryAdapter adapter;
    ArrayList<CategoryDomain> productList = new ArrayList<>();

    public CategoryFragment(ArrayList<CategoryDomain> productList) {
        this.productList = productList;
        // Required empty public constructor
    }


    OptionListener optionListener;

    public void setOptionListener(CategoryFragment.OptionListener optionListener) {
        this.optionListener = optionListener;
    }

    @Override
    public void clicked(String type, Object object) {
        final CategoryDomain productDomain = (CategoryDomain) object;
        if (type.equals("click")) {
            dismiss();
            optionListener.optionSelected(productDomain);
        }
    }

    public interface OptionListener {
        void optionSelected(CategoryDomain categoryDomain);
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

        View contentView = View.inflate(getContext(), R.layout.fragment_category, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SelectCategoryAdapter(getActivity(), productList, this);
        recyclerView.setAdapter(adapter);


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
