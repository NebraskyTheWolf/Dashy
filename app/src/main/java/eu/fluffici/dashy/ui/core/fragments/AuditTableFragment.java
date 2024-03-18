package eu.fluffici.dashy.ui.core.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.evrencoskun.tableview.TableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import eu.fluffici.dashy.R;
import eu.fluffici.dashy.events.module.PaginateCurrentPageEvent;
import eu.fluffici.dashy.events.module.PaginateNextPageEvent;
import eu.fluffici.dashy.events.module.PaginatePrevPageEvent;
import eu.fluffici.dashy.ui.core.adapters.AuditTableAdapter;
import eu.fluffici.dashy.ui.core.listeners.UsersTableViewListener;
import eu.fluffici.dashy.ui.core.viewmodel.audit.AuditViewModel;
import eu.fluffici.dashy.ui.core.viewmodel.audit.AuditViewModelFactory;
import eu.fluffici.data.model.ServiceRequest;
import eu.fluffici.data.util.InjectorUtils;
import com.evrencoskun.tableview.pagination.Pagination;
public class AuditTableFragment extends Fragment {

    private static final String LOG_TAG = AuditTableFragment.class.getSimpleName();

    private final EventBus mBus = EventBus.getDefault();

    private TableView mTableView;
    private AuditTableAdapter mTableAdapter;
    private ProgressBar mProgressBar;
    private AuditViewModel vMainViewModel;

    @Nullable
    private Pagination mPagination; // This is used for paginating the table.

    private int currentPage = 1;

    public AuditTableFragment() {
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
        AuditViewModelFactory factory = InjectorUtils.getAuditViewModelFactory(getActivity().getApplicationContext());
        vMainViewModel = ViewModelProviders.of(this, factory).get(AuditViewModel.class);

        vMainViewModel.getAuditList().observe(getViewLifecycleOwner(), audits -> {

            if(audits != null && !audits.isEmpty()){
                mTableAdapter.setAuditsList(audits);
                hideProgressBar();
            }
        });

        // Let's post a request to get the User data from a web server.
        postRequest(this.currentPage);

        mPagination = new Pagination(mTableView);
        mPagination.setOnTableViewPageTurnedListener(onTableViewPageTurnedListener);

        return view;
    }

    public void nextTablePage() {
        this.postRequest(this.currentPage + 1);
        mPagination.nextPage();
    }

    public void previousTablePage() {
        if (!(this.currentPage <= 1)) {
            this.postRequest(this.currentPage - 1);
            mPagination.previousPage();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        this.mBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        this.mBus.unregister(this);
    }

    private void initializeTableView(TableView tableView){

        // Create TableView Adapter
        mTableAdapter = new AuditTableAdapter(getContext());
        tableView.setAdapter(mTableAdapter);

        // Create listener
        tableView.setTableViewListener(new UsersTableViewListener(tableView));
    }

    @NonNull
    private final Pagination.OnTableViewPageTurnedListener onTableViewPageTurnedListener = new
            Pagination.OnTableViewPageTurnedListener() {
                @Override
                public void onPageTurned(int numItems, int itemsStart, int itemsEnd) {
                    int currentPage = mPagination.getCurrentPage();
                    int pageCount = mPagination.getPageCount();

                    System.out.println(currentPage);
                    System.out.println(pageCount);

                }
    };


    private void postRequest(int page){
        int size = 100; // this is the count of the data items.
        this.currentPage = page;

        this.mBus.post(new PaginateCurrentPageEvent(this.currentPage));

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

    @Subscribe
    public void onNextPage(PaginateNextPageEvent event) {
        this.nextTablePage();
    }

    @Subscribe
    public void onPrevPage(PaginatePrevPageEvent event) {
        this.previousTablePage();
    }
}