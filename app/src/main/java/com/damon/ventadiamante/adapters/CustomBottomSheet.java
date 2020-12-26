package com.damon.ventadiamante.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.interfaces.BuscarClick;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomBottomSheet extends BottomSheetDialogFragment {

    Context context;
    ImageView btn_desde,btn_hasta;
    TextView txv_desde,txv_hasta;
    BuscarClick buscarClick;

    Button btn_buscar;

    long desde,hasta;

    public CustomBottomSheet(Context context,BuscarClick buscarClick) {
        this.context = context;
        this.buscarClick = buscarClick;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_ordenar_fecha,container,false);

        btn_desde = view.findViewById(R.id.btn_desde);
        btn_hasta = view.findViewById(R.id.btn_hasta);
        btn_buscar = view.findViewById(R.id.btn_buscar);
        txv_desde = view.findViewById(R.id.textv_desde);
        txv_hasta = view.findViewById(R.id.textv_hasta);

        calendarioDesde();
        calendarioHasta();

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txv_desde.getText().toString().equals("") && !txv_hasta.getText().toString().equals("")){
                    buscarClick.onClickBuscar(txv_desde.getText().toString(),txv_hasta.getText().toString(),desde,hasta);
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = (FragmentActivity) context;
        super.onAttach(context);
    }

    void calendarioDesde(){
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
//                inputSearch.setText(dateFormatter.format(newDate.getTime()));
                txv_desde.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy ")
                        .format(newDate.getTime()));
                desde = newDate.getTime().getTime();
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        btn_desde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });
    }

    void calendarioHasta(){
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
//                inputSearch.setText(dateFormatter.format(newDate.getTime()));
                txv_hasta.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy ")
                        .format(newDate.getTime()));
                hasta = newDate.getTime().getTime();
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        btn_hasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });
    }
}
