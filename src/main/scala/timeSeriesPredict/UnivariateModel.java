package timeSeriesPredict;

import java.io.Serializable;

/**
 * Created by WanEnFu on 15/12/7.
 */
public class UnivariateModel implements Serializable {

    private long realTimeStamp;
    private long timeStamp;
    private int boltId;
    private int taskNum; // How many bolt will create model at this timeStamp, 如果任務數量大於 bolt, 則 taskNum = totalBoltNum
    private int arimaP;
    private int arimaD;
    private int arimaQ;
    private int garchP;
    private int garchQ;
    private double[] forecast;
    private boolean residualAutocorrelations;
    private boolean residualHeteroscedastic;
    private boolean normality;
    private double aic;
    private double sbc;
    private double rSquare;
    private double rSquareAdjusted;
    private double rmse;
    private double mae;
    private double mape;
    private String packages;
    private boolean emptyModel; // 作為判斷是否為空模型
    private int windowSize;
    private long runTime;

    // 此建構子是產生預設的模型, 其用處為若 R 產生空模型時, 不會導致 null 的發生, 這個預設模型代表的就是空模型了
    public UnivariateModel() {
        this.arimaP = 0;
        this.arimaD = 0;
        this.arimaQ = 0;
        this.garchP = 0;
        this.garchQ = 0;
        this.forecast = new double[0];
        this.residualAutocorrelations = false;
        this.residualHeteroscedastic = false;
        this.normality = false;
        this.aic = 0.0;
        this.sbc = 0.0;
        this.rSquare = 0.0;
        this.rSquareAdjusted = 0.0;
        this.rmse = 0.0;
        this.mae = 0.0;
        this.mape = 0.0;
        this.packages = "";
        this.emptyModel = true;
    }

    // 此建構子是為了測試方便建立假 model 之用
    public UnivariateModel(long a, int b, int c, int d, int e, int f, int g, int h, double[] i, boolean j, boolean k, boolean l, double m, double n, double o, double p, double q, double r, double s, String t, boolean u) {
        this.timeStamp = a;
        this.boltId = b;
        this.taskNum = c;
        this.arimaP = d;
        this.arimaD = e;
        this.arimaQ = f;
        this.garchP = g;
        this.garchQ = h;
        this.forecast = i;
        this.residualAutocorrelations = j;
        this.residualHeteroscedastic = k;
        this.normality = l;
        this.aic = m;
        this.sbc = n;
        this.rSquare = o;
        this.rSquareAdjusted = p;
        this.rmse = q;
        this.mae = r;
        this.mape = s;
        this.packages = t;
        this.emptyModel = u;
    }

    public long getRealTimeStamp() { return realTimeStamp; }

    public void setRealTimeStamp(long realTimeStamp) { this.realTimeStamp = realTimeStamp; }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double[] getForecast() {
        return forecast;
    }

    public void setForecast(double[] forecast) {
        this.forecast = forecast;
    }

    public double getrSquare() {
        return rSquare;
    }

    public void setrSquare(double rSquare) {
        this.rSquare = rSquare;
    }

    public double getAic() {
        return aic;
    }

    public void setAic(double aic) {
        this.aic = aic;
    }

    public double getSbc() {
        return sbc;
    }

    public void setSbc(double sbc) {
        this.sbc = sbc;
    }

    public double getRmse() {
        return rmse;
    }

    public void setRmse(double rmse) {
        this.rmse = rmse;
    }

    public double getMae() {
        return mae;
    }

    public void setMae(double mae) {
        this.mae = mae;
    }

    public double getMape() {
        return mape;
    }

    public void setMape(double mape) {
        this.mape = mape;
    }

    public int getArimaP() {
        return arimaP;
    }

    public void setArimaP(int arimaP) {
        this.arimaP = arimaP;
    }

    public int getArimaD() {
        return arimaD;
    }

    public void setArimaD(int arimaD) {
        this.arimaD = arimaD;
    }

    public int getArimaQ() {
        return arimaQ;
    }

    public void setArimaQ(int arimaQ) {
        this.arimaQ = arimaQ;
    }

    public int getGarchP() {
        return garchP;
    }

    public void setGarchP(int garchP) {
        this.garchP = garchP;
    }

    public int getBoltId() {
        return boltId;
    }

    public void setBoltId(int boltId) {
        this.boltId = boltId;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public boolean isResidualAutocorrelations() {
        return residualAutocorrelations;
    }

    public void setResidualAutocorrelations(boolean residualAutocorrelations) {
        this.residualAutocorrelations = residualAutocorrelations;
    }

    public boolean isResidualHeteroscedastic() {
        return residualHeteroscedastic;
    }

    public void setResidualHeteroscedastic(boolean residualHeteroscedastic) {
        this.residualHeteroscedastic = residualHeteroscedastic;
    }

    public boolean isNormality() {
        return normality;
    }

    public void setNormality(boolean normality) {
        this.normality = normality;
    }

    public double getrSquareAdjusted() {
        return rSquareAdjusted;
    }

    public void setrSquareAdjusted(double rSquareAdjusted) {
        this.rSquareAdjusted = rSquareAdjusted;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public int getGarchQ() {
        return garchQ;
    }

    public void setGarchQ(int garchQ) {
        this.garchQ = garchQ;
    }

    public boolean isEmptyModel() {
        return emptyModel;
    }

    public void setEmptyModel(boolean emptyModel) {
        this.emptyModel = emptyModel;
    }

    public void setWindowSize(int windowSize) {this.windowSize = windowSize; }

    public int getWindowSize() { return windowSize; }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }
}
