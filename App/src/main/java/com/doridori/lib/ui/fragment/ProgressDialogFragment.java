package com.doridori.lib.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment
{
    public static final String ARG_TITLE = "ARG_MSG";
    public static final String ARG_MSG = "ARG_MSG";

    /**
     * @param title can be null
     * @param msg to display
     * @return
     */
    public static ProgressDialogFragment newInstance(String title, String msg)
    {
        ProgressDialogFragment frag = new ProgressDialogFragment ();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MSG, msg);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String title = getArguments().getString(ARG_TITLE);
        String msg = getArguments().getString(ARG_MSG);

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        if(title != null)
            dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    /*
    //================================================================================================
    // DIALOGS
    //================================================================================================

    private static final String FRAGMENT_TAG_CONNECTING_DIALOG = "FRAGMENT_TAG_CONNECTING_DIALOG";

    private void showConnectingDialog()
    {
        hideConnectingDialog();
        FragmentManager fm = getSupportFragmentManager();
        ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(null, "Signing in...");
        progressDialog.show(fm, FRAGMENT_TAG_CONNECTING_DIALOG);
    }

    private void hideConnectingDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fm.findFragmentByTag(FRAGMENT_TAG_CONNECTING_DIALOG);
        if(fragment != null)
            fragment.dismiss();
    }
     */

}