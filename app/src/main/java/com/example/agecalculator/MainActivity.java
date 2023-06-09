package com.example.agecalculator;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText mFirstNameInput;
    private TextView mFirstNameValidationLabel;
    private EditText mLastNameInput;
    private TextView mLastNameValidationLabel;
    private EditText mDOBInput;
    private TextView mDOBValidationLabel;
    private Button mCalculateAgeBtn;
    private static final String DATE_FORMAT = "M/d/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirstNameInput = findViewById(R.id.f_name_input);
        mFirstNameValidationLabel = findViewById(R.id.fname_validation_label);

        mLastNameInput = findViewById(R.id.l_name_input);
        mLastNameValidationLabel = findViewById(R.id.lname_validation_label);

        mDOBInput = findViewById(R.id.dob_input);
        mDOBValidationLabel = findViewById(R.id.dob_validation_label);

        mCalculateAgeBtn = findViewById(R.id.calculate_btn);

        //Add touch listener to the input field because
        //we need the date picker dialog to pop up on touch of DOB input
        mDOBInput.setOnTouchListener(new AgeCalculatorListener());
        mCalculateAgeBtn.setOnClickListener(new AgeCalculatorListener());
    }

    public class AgeCalculatorListener implements View.OnClickListener, View.OnTouchListener {

        private DatePickerDialog datePickerDialog;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.calculate_btn: calculateAge(); break;
                default: break;
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(view.getId() == R.id.dob_input && motionEvent.getAction() == MotionEvent.ACTION_UP) {
                //close regular keyboard if open
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);

                //show date picker dialog
                setSelectedDate(view);
                return true;
            }
            return false;
        }

        private void calculateAge() {
            String fName = mFirstNameInput.getText().toString();
            String lName = mLastNameInput.getText().toString();
            String dob = mDOBInput.getText().toString();
            if(isInputValid(fName, lName, dob)) {
                //hide all validation messages
                mFirstNameValidationLabel.setVisibility(View.GONE);
                mLastNameValidationLabel.setVisibility(View.GONE);
                mDOBValidationLabel.setVisibility(View.GONE);

                //calculate age
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
                LocalDate today = LocalDate.now();
                LocalDate birthday = LocalDate.parse(dob, formatter);

                Period p = Period.between(birthday, today);

                Toast.makeText(MainActivity.this, "You are " + p.getYears() + " years old!", Toast.LENGTH_LONG).show();
            }
        }

        private boolean isInputValid(String fName, String lName, String dob) {
            boolean validFName = validateName(fName, mFirstNameValidationLabel);
            boolean validLName = validateName(lName, mLastNameValidationLabel);
            boolean validDOB = validateDate(dob, mDOBValidationLabel);

            return validFName && validLName && validDOB;
        }

        private boolean validateName(String name, View view) {
            if(isBlank(name) || !isValidName(name)) {
                view.setVisibility(View.VISIBLE);
                return false;
            } else {
                view.setVisibility(View.GONE);
                return true;
            }
        }
        private boolean validateDate(String str, View view) {
            if(isBlank(str)) {
                view.setVisibility(View.VISIBLE);
                return false;
            } else {
                view.setVisibility(View.GONE);
                return true;
            }
        }

        private boolean isBlank(String str) {
            return str == null || str.trim().isEmpty();
        }

        private boolean isValidName(String str) {
            String regex = "[a-zA-Z]+";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(str);
            return m.matches();
        }

        private void setSelectedDate(View view) {
            //Current date - the date picker will be defaulted to this value
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);

            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                    mDOBInput.setText((selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear);
                }
            };

            datePickerDialog = new DatePickerDialog(MainActivity.this, onDateSetListener, year, month, day);
            datePickerDialog.show();
        }
    }
}