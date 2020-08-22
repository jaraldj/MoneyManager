package com.example.moneymanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Authentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {

    //Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating Textview
    private TextView fab_income_text;
    private TextView fab_exppense_text;

    //boolean
    private boolean isOpen = false;
    private Animation FadeOpen, FadeClose;

    //Dashboard income and expense rsult
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;
    private FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter;
    private FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //connect fab to layout
        fab_main_btn = myview.findViewById(R.id.fab_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_ft_btn);

        //connect fab text to layout
        fab_income_text = myview.findViewById(R.id.income_ft_text);
        fab_exppense_text = myview.findViewById(R.id.expense_ft_text);

        //Total Income and Expense result set
        totalIncomeResult = myview.findViewById(R.id.income_result);
        totalExpenseResult = myview.findViewById(R.id.expense_result);

        //Recycler
        mRecyclerIncome = myview.findViewById(R.id.recycler_dash_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_dash_expense);

        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if (isOpen) {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_text.startAnimation(FadeClose);
                    fab_exppense_text.startAnimation(FadeClose);
                    fab_income_text.setClickable(false);
                    fab_exppense_text.setClickable(false);
                    isOpen = false;
                } else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_text.startAnimation(FadeOpen);
                    fab_exppense_text.startAnimation(FadeOpen);
                    fab_income_text.setClickable(true);
                    fab_exppense_text.setClickable(true);
                    isOpen = true;
                }
            }
        });

        //calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalsum = 0;
                for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalsum += data.getAmount();
                    String stResult = String.valueOf(totalsum);
                    totalIncomeResult.setText(stResult + ".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //calculate expense total
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalsum = 0;
                for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalsum += data.getAmount();
                    String strResult = String.valueOf(totalsum);
                    totalExpenseResult.setText(strResult + ".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //Recycler
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    //Floating btn anim
    private void ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_text.startAnimation(FadeClose);
            fab_exppense_text.startAnimation(FadeClose);
            fab_income_text.setClickable(false);
            fab_exppense_text.setClickable(false);
            isOpen = false;
        } else {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_text.startAnimation(FadeOpen);
            fab_exppense_text.startAnimation(FadeOpen);
            fab_income_text.setClickable(true);
            fab_exppense_text.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_inputdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText editAmount = myview.findViewById(R.id.amount_edit);
        final EditText editType = myview.findViewById(R.id.type_edit);
        final EditText editNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    editType.setError("Required Field....");
                    return;
                }

                if (TextUtils.isEmpty(amount)) {
                    editAmount.setError("Required Field....");
                    return;
                }

                Long ouramountint = Long.parseLong(amount);

                if (TextUtils.isEmpty(note)) {
                    editNote.setError("Required Field....");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ouramountint, type, note, id, mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();

                ftAnimation();

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_inputdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText amount = myview.findViewById(R.id.amount_edit);
        final EditText type = myview.findViewById(R.id.type_edit);
        final EditText note = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Required Field....");
                    return;
                }

                Long inamount = Long.parseLong(tmAmount);
                if (TextUtils.isEmpty(tmType)) {
                    type.setError("Required Field....");
                    return;
                }


                if (TextUtils.isEmpty(tmNote)) {
                    note.setError("Required Field....");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(inamount, tmType, tmNote, id, mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        Log.e("hello dshboard", "hi"+mExpenseDatabase.toString());
        super.onStart();

        FirebaseRecyclerOptions<Data> incomeOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, new SnapshotParser<Data>() {
                    @NonNull
                    @Override
                    public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Data((Long) snapshot.child("amount").getValue(),
                                snapshot.child("type").getValue().toString(),
                                snapshot.child("note").getValue().toString(),
                                snapshot.child("id").getValue().toString(),
                                snapshot.child("date").getValue().toString());
                    }
                })
                .build();

        incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (incomeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycler_data, parent, false);
                return new IncomeViewHolder(view);
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        FirebaseRecyclerOptions<Data> expenseOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, new SnapshotParser<Data>() {
                    @NonNull
                    @Override
                    public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Data((Long) snapshot.child("amount").getValue(),
                                snapshot.child("type").getValue().toString(),
                                snapshot.child("note").getValue().toString(),
                                snapshot.child("id").getValue().toString(),
                                snapshot.child("date").getValue().toString());
                    }
                })
                .build();

        expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (expenseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {

                holder.setExpenseType(model.getType());
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycler_data, parent, false);

                return new ExpenseViewHolder(view);
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (incomeAdapter != null)
            incomeAdapter.stopListening();

        if (expenseAdapter != null)
            expenseAdapter.stopListening();
    }

    //for Income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        View mIncomeView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type) {
            TextView mtype = mIncomeView.findViewById(R.id.type_txt_income);
            mtype.setText(type);
        }

        public void setIncomeAmount(Long amount) {
            TextView mamount = mIncomeView.findViewById(R.id.amount_txt_income);
            String strAmount = String.valueOf(amount);
            mamount.setText(strAmount);
        }

        public void setIncomeDate(String date) {
            TextView mDate = mIncomeView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

    }

    //for Expense data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type) {
            TextView mtype = mExpenseView.findViewById(R.id.type_txt_expense);
            mtype.setText(type);
        }

        public void setExpenseAmount(Long amount) {
            TextView mamount = mExpenseView.findViewById(R.id.amount_txt_expense);
            String strAmount = String.valueOf(amount);
            mamount.setText(strAmount);
        }

        public void setExpenseDate(String date) {
            TextView mDate = mExpenseView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
    }
}
