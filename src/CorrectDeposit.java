package khodkov.michael.Fundraising;

public class CorrectDeposit {

    public String setCorrectDeposit(String st){
        String result = "0.0";
        try {
            float fl = Float.parseFloat(st);
            fl = fl * 100f;
            int value = (int)Math.round(fl);
            fl = value / 100f;
            result = String.valueOf(fl);
        }catch (Exception e){
            return result;
        }
        return result;
    }
}
