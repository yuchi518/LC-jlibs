package lets.cool.util;

public class HumanReadableText {

    public static String byteCount(long bytes) {
        return byteCount(bytes, false);
    }

    public static String byteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + "B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1);
        return String.format("%.1f%cB", bytes / Math.pow(unit, exp), pre);
    }

    public static String elapsedTime(long time) {
        if (time < 60*1000) {
            return String.format("%02d.%03ds", time/1000%60, time%1000);
        } else if (time < 60*60*1000) {
            return String.format("%02dm:%02ds", time/1000/60, time/1000%60);
        } else {
            return String.format("%02dh:%02dm", time/1000/60/60, time/1000/60%60);
        }
    }

    public static String elapsedTimeDetail(long time) {
        if (time < 60*1000) {
            return String.format("%02d.%03ds", time/1000%60, time%1000);
        } else if (time < 60*60*1000) {
            return String.format("%02dm:%02d.%03ds", time/1000/60, time/1000%60, time%1000);
        } else {
            return String.format("%02dh:%02dm:%02d.%03ds", time/1000/60/60, time/1000/60%60, time/1000%60, time%1000);
        }
    }
}
