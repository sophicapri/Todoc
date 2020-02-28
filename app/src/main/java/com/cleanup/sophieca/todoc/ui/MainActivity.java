package com.cleanup.sophieca.todoc.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cleanup.sophieca.todoc.R;
import com.cleanup.sophieca.todoc.TaskViewModel;
import com.cleanup.sophieca.todoc.injection.DI;
import com.cleanup.sophieca.todoc.injection.Injection;
import com.cleanup.sophieca.todoc.model.Employee;
import com.cleanup.sophieca.todoc.utils.ViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SOPHIE on 25/02/2020.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TaskViewModel viewModel;
    public static String EMPLOYEE_ID_EXTRA = "employee id";
    public static Employee CURRENT_EMPLOYEE = null;
    private String emailEmployee;
    private String passwordEmployee;
    public Context context;
    private ProgressDialog progressDialog;

    @BindView(R.id.email_input)
    EditText emailInput;
    @BindView(R.id.password_input)
    EditText passwordInput;
    @BindView(R.id.sign_in_btn)
    Button signInBtn;
    @BindView(R.id.sign_up_btn)
    Button signUpBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureViewModel();
        DI.setViewModel(viewModel);
        ButterKnife.bind(this);

        context = this;
        progressDialog = new ProgressDialog(this);
        DI.initProgressDialog(progressDialog);

        signInBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

        viewModel.createEmployee(DI.getDummyEmployees().get(0));
        viewModel.createEmployee(DI.getDummyEmployees().get(1));

        if (CURRENT_EMPLOYEE != null)
            startTaskActivity();
    }

    private void signInOrSignUp() {
        emailEmployee = emailInput.getText().toString();
        passwordEmployee = passwordInput.getText().toString();
    }


    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(TaskViewModel.class);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_btn) {
            signInOrSignUp();
            observeEmployee();
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (CURRENT_EMPLOYEE == null) {
                        Toast.makeText(context, context.getString(R.string.user_unrecognised), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.welcome) +
                                CURRENT_EMPLOYEE.getFirstname() + " ! :)", Toast.LENGTH_SHORT).show();
                        startTaskActivity();
                    }
                    progressDialog.dismiss();
                }
            }, 1000);
        } else {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    private void observeEmployee() {
        viewModel.getEmployee(emailEmployee, passwordEmployee).observe(this, new Observer<Employee>() {
            @Override
            public void onChanged(Employee employee) {
                CURRENT_EMPLOYEE = employee;
            }
        });
    }

    private void startTaskActivity() {
        Intent intent = new Intent(MainActivity.this, TasksListActivity.class);
        intent.putExtra(EMPLOYEE_ID_EXTRA, CURRENT_EMPLOYEE.getId());
        startActivity(intent);
        finish();
    }
}
