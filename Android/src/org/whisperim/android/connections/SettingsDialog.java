package org.whisperim.android.connections;

import org.whisperim.android.R;
import org.whisperim.android.ui.Controller;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class SettingsDialog extends Dialog implements android.view.View.OnClickListener {
    private Context xmppClient;
    private Controller c_;

    public SettingsDialog(Context xmppClient, Controller c) {
        super(xmppClient);
        this.xmppClient = xmppClient;
        c_ = c;
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.settings);
        getWindow().setFlags(4, 4);
        setTitle("XMPP Settings");
        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }

    public void onClick(View v) {
        
        String username = getText(R.id.userid);
        String password = getText(R.id.password);
        c_.signOnGtalk(username, password);
        

       
        dismiss();
    }

    private String getText(int id) {
        EditText widget = (EditText) this.findViewById(id);
        return widget.getText().toString();
    }
}
