package com.yc.english.group.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.yc.english.R;
import com.yc.english.base.helper.TipsHelper;
import com.yc.english.group.constant.GroupConstant;
import com.yc.english.group.model.bean.GroupInfoHelper;

import java.util.HashSet;
import java.util.Iterator;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.activity.FileListActivity;
import io.rong.imkit.model.FileInfo;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * Created by wanglin  on 2017/7/24 15:54.
 * 文件
 */

public class FilePlugin implements IPluginModule {
    private Conversation.ConversationType conversationType;
    private String targetId;
    private FragmentActivity activity;

    @Override
    public Drawable obtainDrawable(Context context) {

        return ContextCompat.getDrawable(context,R.drawable.group_file_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return "文件";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        this.conversationType = rongExtension.getConversationType();
        this.targetId = rongExtension.getTargetId();
        activity = fragment.getActivity();

        if (GroupInfoHelper.getClassInfo().getIs_allow_talk()==0){
            TipsHelper.tips(activity, GroupConstant.FORBID_CONTENT);
            return;
        }

        String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
        if (PermissionCheckUtil.requestPermissions(fragment, permissions)) {
            Intent intent = new Intent(activity, FileListActivity.class);
            intent.putExtra("rootDirType", 100);
            intent.putExtra("fileFilterType", 5);
            intent.putExtra("fileTraverseType", 201);
            rongExtension.startActivityForPluginResult(intent, 100, this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 100 && intent != null) {
            HashSet selectedFileInfos = (HashSet) intent.getSerializableExtra("selectedFiles");
            Iterator var5 = selectedFileInfos.iterator();

            while (var5.hasNext()) {
                FileInfo fileInfo = (FileInfo) var5.next();
                Uri filePath = Uri.parse("file://" + fileInfo.getFilePath());

                /**
                 *  生成 Message 对象。
                 * "7127" 为目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
                 * Conversation.ConversationType.PRIVATE 为私聊会话类型，根据需要，也可以传入其它会话类型，如群组，讨论组等。
                 */
                FileMessage fileMessage = FileMessage.obtain(filePath);
                if (fileMessage != null) {
                    fileMessage.setType(fileInfo.getSuffix());
                    Message message = Message.obtain(this.targetId, this.conversationType, fileMessage);

                    /**
                     * <p>发送消息。
                     * 通过 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}
                     * 中的方法回调发送的消息状态及消息体。</p>
                     *
                     * @param message     将要发送的消息体。
                     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
                     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
                     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
                     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
                     * @param callback    发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
                     */
                    RongIM.getInstance().sendMediaMessage(message, null, null, new IRongCallback.ISendMediaMessageCallback() {


                        @Override
                        public void onProgress(Message message, int i) {

                        }

                        @Override
                        public void onCanceled(Message message) {

                        }

                        @Override
                        public void onAttached(Message message) {
                            //消息本地数据库存储成功的回调
                        }

                        @Override
                        public void onSuccess(Message message) {
                            //消息通过网络发送成功的回调
                            // TODO: 2017/7/19 可以做上传操作
                            Toast.makeText(activity, "上传成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                            //消息发送失败的回调
                            Toast.makeText(activity, "上传失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}

