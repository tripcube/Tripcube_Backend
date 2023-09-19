package uos.ac.kr.repositories;
import org.springframework.security.core.parameters.P;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uos.ac.kr.dtos.GetPlaceDTO;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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


   static public String getLocationPlace(double mapX, double mapY, int page) {
        String value = "";
        String uri = "http://apis.data.go.kr/B551011/KorService1/locationBasedList1?serviceKey=QLp3nZEg9kI557QS69hIyn6tbE5Stw%2BfSjkIX8RxQPoix2Unp3ZtIkVmVZsre5BqTaLEVBH4X9oK4Lcp7VMjuQ%3D%3D&numOfRows=10&MobileOS=ETC&MobileApp=AppTest&_type=Json&radius=1000";
        uri = uri + "&mapX=" + mapX + "&mapY=" + mapY + "&pageNo=" + page;

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

        return value;
   }

    static public String getKeywordPlace(String keyword, int page) {
        String value = "";
        String uri = "http://apis.data.go.kr/B551011/KorService1/searchKeyword1?serviceKey=QLp3nZEg9kI557QS69hIyn6tbE5Stw%2BfSjkIX8RxQPoix2Unp3ZtIkVmVZsre5BqTaLEVBH4X9oK4Lcp7VMjuQ%3D%3D&numOfRows=10&MobileOS=ETC&MobileApp=AppTest&listYN=Y";
        uri = uri + "&keyword=" + URLEncoder.encode(keyword) + "&pageNo=" + page;

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

        return value;
    }

    //공통정보
    static public String getPlaceDetail(int placeId) {
        String value = "";
        String uri = "http://apis.data.go.kr/B551011/KorService1/detailCommon1?serviceKey=QLp3nZEg9kI557QS69hIyn6tbE5Stw%2BfSjkIX8RxQPoix2Unp3ZtIkVmVZsre5BqTaLEVBH4X9oK4Lcp7VMjuQ%3D%3D&MobileOS=ETC&MobileApp=AppTest&defaultYN=Y&firstImageYN=Y&addrinfoYN=Y&mapinfoYN=Y&areacodeYN=Y";
        uri = uri + "&contentId=" + placeId;

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

        return value;
    }

    //소개정보
    static public String getPlaceDetail2(int placeId, int contentType) {
        String value = "";
        String uri = "http://apis.data.go.kr/B551011/KorService1/detailIntro1?serviceKey=QLp3nZEg9kI557QS69hIyn6tbE5Stw%2BfSjkIX8RxQPoix2Unp3ZtIkVmVZsre5BqTaLEVBH4X9oK4Lcp7VMjuQ%3D%3D&MobileOS=ETC&MobileApp=AppTest";
        uri = uri + "&contentId=" + placeId + "&contentTypeId=" + contentType;

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

        return value;
    }

    static public String getPlaceImages(int placeId) {
        String value = "";
        String uri = "http://apis.data.go.kr/B551011/KorService1/detailImage1?serviceKey=QLp3nZEg9kI557QS69hIyn6tbE5Stw%2BfSjkIX8RxQPoix2Unp3ZtIkVmVZsre5BqTaLEVBH4X9oK4Lcp7VMjuQ%3D%3D&MobileOS=ETC&MobileApp=AppTest&numOfRows=10&imageYN=Y&subImageYN=Y";
        uri = uri + "&contentId=" + placeId;

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

        return value;
    }

}
