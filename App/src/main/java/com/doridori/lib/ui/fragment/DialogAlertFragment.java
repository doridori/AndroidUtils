package com.doridori.lib.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * A Generic quick to use AlertDialog Fragment.
 *
 * Remember to implement button callbacks as interface so not tied to any specific activity / fragment instance. Use GetParentFragment and getActivity and cast to callback interface for any callbacks - this will work on config changes.yes
 */
public class DialogAlertFragment extends DialogFragment
{
    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_MSG = "ARG_MSG";
    public static final String ARG_POSITIVE_BUTTON_TXT = "ARG_POSITIVE_BUTTON_TXT";

    /**
     * @param title can be null
     * @param msg to display
     * @return
     */
    public static DialogFragment newInstance(String title, String msg, String positiveButtonText)
    {
        DialogAlertFragment frag = new DialogAlertFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MSG, msg);
        args.putString(ARG_POSITIVE_BUTTON_TXT, positiveButtonText);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String title = getArguments().getString(ARG_TITLE);
        String msg = getArguments().getString(ARG_MSG);
        String positiveButton = getArguments().getString(ARG_POSITIVE_BUTTON_TXT);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(title != null)
            builder.setTitle(title);
        builder.setMessage(msg);
        if(positiveButton != null)
            builder.setPositiveButton(positiveButton, null);
        return builder.show();
    }
}
