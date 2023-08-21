package uos.ac.kr.repositories;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceRepository {

   static public String getPlaceName(int placeId) {
       String returnValue;
       String value = "";
       String uri = "http://apis.data.go.kr/B551011/KorService1/detailCommon1?serviceKey=QLp3nZEg9kI557QS69hIyn6tbE5Stw%2BfSjkIX8RxQPoix2Unp3ZtIkVmVZsre5BqTaLEVBH4X9oK4Lcp7VMjuQ%3D%3D&numOfRows=10&pageNo=0&MobileOS=ETC&MobileApp=AppTest&defaultYN=Y";
       uri = uri + "&ContentId=" + placeId;
       
        try {
            URL obj = new URL(uri);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();

            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                value = value + line + "\n";
            }

        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(value)));

            NodeList nodeList = document.getElementsByTagName("title");
            Node textNode = nodeList.item(0).getChildNodes().item(0);
            returnValue = textNode.getNodeValue();
        } catch (Exception e) {
            throw new ResourceNotFoundException("placeId에 해당되는 장소가 없습니다.");
        }

        return returnValue;
    }
}
