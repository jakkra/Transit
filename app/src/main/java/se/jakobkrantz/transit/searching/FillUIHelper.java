package se.jakobkrantz.transit.searching;/*
 * Created by krantz on 14-11-26.
 */

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.skanetrafikenAPI.RouteLink;
import se.jakobkrantz.transit.skanetrafikenAPI.TimeAndDateConverter;

public class FillUIHelper {
    private TextView transportNameNbr; //Stadsbuss 4
    private TextView timeBetween; //Jounrey between start - end
    private TextView nbrDepTime; //Time to departure
    private TextView message;
    private TextView timeToDep;
    private TextView delayMin;
    private ImageView firstPicType;
    private ImageView clock;


    private List<ImageView> routePics;
    private List<TextView> routeNbrs;


    public FillUIHelper(View v) {
        routeNbrs = new ArrayList<TextView>();
        routePics = new ArrayList<ImageView>();

        transportNameNbr = (TextView) v.findViewById(R.id.transport_name_nbr);
        timeBetween = (TextView) v.findViewById(R.id.time_between);
        nbrDepTime = (TextView) v.findViewById(R.id.nbr_dep_time);
        message = (TextView) v.findViewById(R.id.message);
        timeToDep = (TextView) v.findViewById(R.id.time_to_dep);
        delayMin = (TextView) v.findViewById(R.id.delay_min);
        clock = (ImageView) v.findViewById(R.id.iw_clock);
        clock.setPadding(11, 11, 11, 11);
        firstPicType = (ImageView) v.findViewById(R.id.iw_first_bus_type);

        routeNbrs.add((TextView) v.findViewById(R.id.stop_view));
        routeNbrs.add((TextView) v.findViewById(R.id.note_view));
        routeNbrs.add((TextView) v.findViewById(R.id.textView7));
        routeNbrs.add((TextView) v.findViewById(R.id.textView8));
        routeNbrs.add((TextView) v.findViewById(R.id.textView9));
        routeNbrs.add((TextView) v.findViewById(R.id.textView10));

        routePics.add((ImageView) v.findViewById(R.id.imageView1));
        routePics.add((ImageView) v.findViewById(R.id.imageView2));
        routePics.add((ImageView) v.findViewById(R.id.imageView3));
        routePics.add((ImageView) v.findViewById(R.id.imageView4));
        routePics.add((ImageView) v.findViewById(R.id.circleView));
        routePics.add((ImageView) v.findViewById(R.id.imageView6));
        routePics.add((ImageView) v.findViewById(R.id.imageView7));
        routePics.add((ImageView) v.findViewById(R.id.imageView8));
        routePics.add((ImageView) v.findViewById(R.id.imageView9));
        routePics.add((ImageView) v.findViewById(R.id.imageView10));
        routePics.add((ImageView) v.findViewById(R.id.imageView11));
    }

    public void updateUI(Journey j) {
        for (ImageView iw : routePics) {
            iw.setImageDrawable(null);
        }
        for (TextView tw : routeNbrs) {
            tw.setText("");
        }
        if (j.getFirstRouteLineNbr().contains("SkåneExpressen")) {
            transportNameNbr.setText(j.getFirstRouteLineNbr());
        } else {
            transportNameNbr.setText(j.getFirstRouteTransportName() + " " + j.getFirstRouteLineNbr());
        }
        timeBetween.setText(TimeAndDateConverter.formatTime(j.getDepDateTime()) + " - " + TimeAndDateConverter.formatTime(j.getArrDateTime()));
        nbrDepTime.setText(j.getTotalTravelTime() + " min ");
        String smartMessage = j.getSmartMessage();
        if (smartMessage.length() > 80) {
            message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        } else {
            message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        }
        message.setText(j.getSmartMessage());
        timeToDep.setText(" om " + j.getTimeToDep());


        delayMin.setText(j.getDeviationDepTime());
        firstPicType.setImageResource(getDrawableFromLineType(j.getFirstRouteLineId()));

        if (j.deviationType() != RouteLink.UNKNOWN_DEVIATION) {
            clock.setImageResource(getDrawableFromDeviation(j.deviationType()));
        } else {
            clock.setImageDrawable(null);
        }

        List<String> lineNbrs = j.getChangeNbrs();
        List<Integer> lineTypes = j.getLineTypes();
        for (int i = 0; i < lineTypes.size(); i++) {
            routePics.get(i * 2).setImageResource(getDrawableFromLineType(lineTypes.get(i)));
            if (lineNbrs.get(i).length() < 6) {
                routeNbrs.get(i).setText(lineNbrs.get(i));
            } else {
                String nbr = lineNbrs.get(i).replaceAll("[^\\d.]", "");
                routeNbrs.get(i).setText(nbr);
            }
            routeNbrs.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        }
        if (lineTypes.size() != 1) {


            for (int i = 0; i < lineTypes.size(); i++) {
                if (i != (lineTypes.size() - 1)) {
                    routePics.get(1 + 2 * i).setImageResource(R.drawable.ic_next);
                    routePics.get(1 + 2 * i).setPadding(20, 20, 20, 20);
                }
            }
        }
    }

    public static int getDrawableFromDeviation(int devType) {
        switch (devType) {
            case RouteLink.IN_TIME:
                return R.drawable.ic_clock_green;
            case RouteLink.EARLY:
                return R.drawable.ic_clock_blue;
            case RouteLink.LATE:
                return R.drawable.ic_clock_red;
            default:
                return 0;
        }
    }

    public static int getDrawableFromLineType(int lineType) {
        switch (lineType) {
            //case RouteLink.PENDELN:
            //    return R.drawable.ic_bus_region;
            case RouteLink.REGIONBUSS:
                return R.drawable.ic_bus_region;
            case RouteLink.PÅGATÅG_EXPRESS:
                return R.drawable.ic_train_pagatag;
            case RouteLink.PÅGATÅGEN:
                return R.drawable.ic_train_pagatag;
            case RouteLink.ÖRESUNDSTÅG:
                return R.drawable.ic_train_oresund;
            case RouteLink.STADSBUSS:
                return R.drawable.ic_bus_local;
            case RouteLink.SKÅNE_EXPRESSEN:
                return R.drawable.ic_bus_region;
            default:
                return R.drawable.ic_walking;


        }
    }

}
