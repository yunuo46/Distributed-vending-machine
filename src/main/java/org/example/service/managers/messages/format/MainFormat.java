package org.example.service.managers.messages.format;

import java.util.Map;

public abstract class MainFormat {
    protected String msg_type;
    protected String src_id;
    protected String dst_id;
    protected Map<String, Object>  msg_content;
}
