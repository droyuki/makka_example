package timeSeriesPredict;

import java.io.Serializable;

/**
 * Created by WanEnFu on 15/12/7.
 */

// 此 Class 即為 arima(p, d, q) 中的 (p, d, q)
public class ArmaParameter implements Serializable {

    private int acf;
    private int d;
    private int pacf;

    public ArmaParameter(int acf, int d, int pacf) {
        this.acf = acf;
        this.d = d;
        this.pacf = pacf;
    }

    public int getAcf() {
        return acf;
    }

    public int getD() { return d; }

    public int getPacf() {
        return pacf;
    }

}
