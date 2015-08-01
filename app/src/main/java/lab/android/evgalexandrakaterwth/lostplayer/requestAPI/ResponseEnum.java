package lab.android.evgalexandrakaterwth.lostplayer.requestAPI;

/**
 * Created by ekaterina on 26.07.2015.
 */
public enum ResponseEnum {
    OK(200),
    CREATED(201),
    NOCONTENT(204),
    CONFLICT(409),
    BADREQUEST(400),
    UNAUTHORIZED(401),
    NOTFOUND(404),
    SERVICEUNAVAILABLE(503);

    private final int code;

    private ResponseEnum(int code) {
        this.code = code;
    }

    public static ResponseEnum getResponseEnumByCode(int code) {
        for (ResponseEnum response : values()) {
            if (response.getCode() == code) {
                return response;
            }
        }
        return null;
    }

    public int getCode() {
        return this.code;
    }
}