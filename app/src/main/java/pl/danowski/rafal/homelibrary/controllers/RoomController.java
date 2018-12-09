package pl.danowski.rafal.homelibrary.controllers;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import pl.danowski.rafal.homelibrary.model.room.CreateRoom;
import pl.danowski.rafal.homelibrary.model.room.Room;

public class RoomController {

    private RestTemplate mRestTemplate = new RestTemplate();

    public boolean createRoom(CreateRoom room) {
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<Room> exchange = mRestTemplate.exchange(Urls.getCreateRoomUrl(), HttpMethod.POST, new HttpEntity<>(room), Room.class);
        HttpStatus statusCode = exchange.getStatusCode();
        return statusCode != HttpStatus.OK;
    }

    public void updateRoom(Room room) {
        String url = Urls.getFindDeleteUpdateRoomById(room.getId());
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(room), Room.class);
    }

    public Room findRoomById(int id) {
        String url = Urls.getFindDeleteUpdateRoomById(id);
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<Room> exchange;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(""), Room.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpStatus statusCode = exchange.getStatusCode();

        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }

    public List<Room> findRoomsByUserLogin(String login) {
        String url = Urls.getFindRoomsByUserLoginUrl(login);
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<List<Room>> exchange;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Room>>(){});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpStatus statusCode = exchange.getStatusCode();

        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }

    public void deleteRoom(int id) {
        String url = Urls.getFindDeleteUpdateRoomById(id);
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.exchange(url, HttpMethod.DELETE, null, Room.class);
    }

}
