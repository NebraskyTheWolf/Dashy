package eu.fluffici.dashy.ui.core.fragments;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.evrencoskun.tableview.TableView;

import eu.fluffici.dashy.R;
import eu.fluffici.dashy.ui.core.adapters.UsersTableAdapter;
import eu.fluffici.dashy.ui.core.listeners.UsersTableViewListener;
import eu.fluffici.dashy.ui.core.viewmodel.users.MainViewModel;
import eu.fluffici.dashy.ui.core.viewmodel.users.MainViewModelFactory;
import eu.fluffici.data.model.ServiceRequest;
import eu.fluffici.data.util.InjectorUtils;

public class TableFragment extends Fragment {

    private static final String LOG_TAG = TableFragment.class.getSimpleName();

    private TableView mTableView;
    private UsersTableAdapter mTableAdapter;
    private ProgressBar mProgressBar;
    private MainViewModel vMainViewModel;

    public TableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mProgressBar = view.findViewById(R.id.progressBar);

        mTableView = view.findViewById(R.id.my_TableView);

        initializeTableView(mTableView);


        // initialize ViewModel
        MainViewModelFactory factory = InjectorUtils.getMainViewModelFactory(getActivity().getApplicationContext());
        vMainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        vMainViewModel.getUserList().observe(getViewLifecycleOwner(), users -> {

            if(users != null && !users.isEmpty()){
                // set the list on TableViewModel
                mTableAdapter.setUserList(users);

                hideProgressBar();
            }
        });

        // Let's post a request to get the User data from a web server.
        postRequest();

        return view;
    }


    private void initializeTableView(TableView tableView){

        // Create TableView Adapter
        mTableAdapter = new UsersTableAdapter(getContext());
        tableView.setAdapter(mTableAdapter);

        // Create listener
        tableView.setTableViewListener(new UsersTableViewListener(tableView));
    }


    private void postRequest(){
        int size = 100; // this is the count of the data items.
        int page = 1; // Which page do we want to get from the server.
        ServiceRequest serviceRequest = new ServiceRequest(size, page);
        vMainViewModel.postRequest(serviceRequest);

        showProgressBar();
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTableView.setVisibility(View.INVISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTableView.setVisibility(View.VISIBLE);
    }
}