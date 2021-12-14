package cat.udl.tidic.amb.janari0android;

public class LocationUser {
    String geohash;
    double lat;
    double lon;
    public LocationUser(String geohash, double lat, double lon) {
        this.geohash = geohash;
        this.lat = lat;
        this.lon = lon;
    }
    public LocationUser(){}
    public String getGeohash(){return geohash;}
    public double getLat() { return lat; }
    public double getLon() { return lon; }
}
