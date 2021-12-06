package com.udacity.moonstore.storeItems.list

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.udacity.moonstore.R
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.authentication.AuthenticationActivity
import com.udacity.moonstore.authentication.AuthenticationHelper
import com.udacity.moonstore.base.BaseFragment
import com.udacity.moonstore.data.StockNotificationHelper.getStockNotificationPreference
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.databinding.FragmentStoreListBinding
import com.udacity.moonstore.storeItems.StoreViewModel
import com.udacity.moonstore.storeItems.StoreActivity
import com.udacity.moonstore.storeItems.StoreListViewModel
import com.udacity.moonstore.utils.setDisplayHomeAsUpEnabled
import com.udacity.moonstore.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreListFragment : BaseFragment() {

    override val _viewModel: StoreListViewModel by viewModel()
    private lateinit var storeViewModel: StoreViewModel

    private lateinit var binding: FragmentStoreListBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_store_list, container, false
            )
        binding.viewModel = _viewModel
        storeViewModel =
            ViewModelProvider(requireActivity() as StoreActivity)[StoreViewModel::class.java]

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))
        navController = findNavController()

        _viewModel.showLoading.observe(this, { showLoading ->
            binding.refreshLayout.isRefreshing = showLoading
        })

        binding.refreshLayout.setOnRefreshListener {
            _viewModel.loadStoreItems()
        }
        if (getStockNotificationPreference(requireActivity())
            == StockNotificationStatus.NEVER_DEFINED.name
        ) {
            createDialogWantsNotification()
        }

        return binding.root
    }

    private fun createDialogWantsNotification() {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.stock_notif_proposal_title))
        builder.setMessage(getString(R.string.stock_notif_proposal_msg))

        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            Toast.makeText(
                context,
                getString(R.string.notif_on), Toast.LENGTH_SHORT
            ).show()
            storeViewModel.updateStockNotificationStatus(
                requireActivity(),
                StockNotificationStatus.NOTIF_ON
            )
        }

        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
            showDefineNotifLaterDialog()
            storeViewModel.updateStockNotificationStatus(
                requireActivity(),
                StockNotificationStatus.NOTIF_OFF
            )
        }
        builder.show()
    }

    private fun showDefineNotifLaterDialog() {
        val setupLaterDialog = AlertDialog.Builder(context).create()
        setupLaterDialog.setTitle(R.string.stock_notif_proposal_title)
        setupLaterDialog.setMessage(getString(R.string.stock_notif_later_msg))
        setupLaterDialog.setButton(
            DialogInterface.BUTTON_NEUTRAL,
            getString(R.string.ok)
        ) { _, _ -> setupLaterDialog.dismiss() }
        setupLaterDialog.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadStoreItems()
    }

    private fun setupRecyclerView() {
        val adapter = StoreListAdapter({ storeItem ->
            _viewModel.changeFavoriteStatus(storeItem)
        }, { storeItem ->
            navController.navigate(StoreListFragmentDirections.toStoreItemDetails(storeItem))
        })

        binding.storeRecyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout -> {
                AuthenticationHelper
                    .signOut(requireContext())
                    .addOnCompleteListener {
                        val intent = Intent(this.context, AuthenticationActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
            }
            R.id.myFavoritesMenu -> _viewModel.updateFilter(StoreItemFilter.FAVORITES)
            R.id.allItemsMenu -> _viewModel.updateFilter(StoreItemFilter.ALL)
            R.id.settingsMenu -> navController.navigate(StoreListFragmentDirections.toSettings())

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

}
