package com.online.estoreshop.volleyservice;

import org.json.JSONObject;

public interface NotiInterface {
    void NotificationSuccess(JSONObject response, int requestcode);

    void NotificationError(String msg, int requestcode);

}
