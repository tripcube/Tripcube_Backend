package uos.ac.kr.utils;


import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class FcmUtil {
    private final FirebaseMessaging firebaseMessaging;
    public void sendFCM(String FCMToken, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(FCMToken)
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            System.out.println("토큰 값이 부정확하여 메시지 전송에 실패했습니다.");
        }

    }
}
