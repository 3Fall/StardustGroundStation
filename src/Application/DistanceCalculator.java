package Application;

public class DistanceCalculator {
    public static float calculateDistance(float lat1, float lon1, float lat2, float lon2) {
        float earth_radius = 6378100;

        float d_lat = (float) (((lat2-lat1) * Math.PI) / 180);
        float d_lon = (float) (((lon2-lon1) * Math.PI) / 180);

        lat1 = (float) ((lat1 * Math.PI) / 180);
        lat2 = (float) ((lat2 * Math.PI) / 180);

        float a = (float) (Math.sin(d_lat/2) * Math.sin(d_lat/2) + Math.sin(d_lon/2) * Math.sin(d_lon/2) * Math.cos(lat1) * Math.cos(lat2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));

        return earth_radius * c;
    }
}
