package com.online.estoreshop.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
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
import com.online.estoreshop.models.ProductDomain;

import java.util.ArrayList;

public class ProductFragment extends BottomSheetDialogFragment implements ClickedItem {

    String msg;
    SelectProductAdapter adapter;
    ArrayList<ProductDomain> productList = new ArrayList<>();
    ArrayList<ProductDomain> selectedList = new ArrayList<>();
    TextView tvNum;

    public ProductFragment(ArrayList<ProductDomain> productList) {
        this.productList = productList;
        // Required empty public constructor
    }


    OptionListener3 optionListener;

    public void setOptionListener3(ProductFragment.OptionListener3 optionListener) {
        this.optionListener = optionListener;
    }

    @Override
    public void clicked(String type, Object object) {
        final ProductDomain productDomain = (ProductDomain) object;
        if (type.equals("check")) {

            if (selectedList.contains(productDomain)) {
                selectedList.remove(productDomain);
            } else {
                selectedList.add(productDomain);
            }

            tvNum.setText(selectedList.size() + "/" + productList.size() + " Selected");


//            dismiss();
//            optionListener.optionSelected3(productDomain);
        }
    }

    public interface OptionListener3 {
        void optionSelected3(ArrayList<ProductDomain> selectedList);
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

        View contentView = View.inflate(getContext(), R.layout.fragment_product, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        tvNum = contentView.findViewById(R.id.tvNum);
        TextView btnSubmit = contentView.findViewById(R.id.btnSubmit);

        tvNum.setText("0/" + productList.size() + " Selected");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        adapter = new SelectProductAdapter(getActivity(), productList, this);
        recyclerView.setAdapter(adapter);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.size() == 0) {
                    Toast.makeText(getActivity(), "Not selected any products", Toast.LENGTH_SHORT).show();
                } else {
                    dismiss();
                    optionListener.optionSelected3(selectedList);
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
