package se.jakobkrantz.transit.app.searching;/*
 * Created by krantz on 14-12-02.
 */


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.RouteLink;
import se.jakobkrantz.transit.app.skanetrafikenAPI.TimeAndDateConverter;

import java.util.ArrayList;
import java.util.List;

public class FillDetailedHeaderHelper {

    private TextView timeBetween;
    private TextView nbrDepTime;
    private TextView timeToDep;

    private List<ImageView> routePics;
    private List<TextView> routeNbrs;

    public FillDetailedHeaderHelper(View v) {
        routeNbrs = new ArrayList<TextView>();
        routePics = new ArrayList<ImageView>();

        timeBetween = (TextView) v.findViewById(R.id.time_between);
        nbrDepTime = (TextView) v.findViewById(R.id.nbr_dep_time);
        timeToDep = (TextView) v.findViewById(R.id.time_to_dep);

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
        timeBetween.setText(TimeAndDateConverter.formatTime(j.getDepDateTime()) + " - " + TimeAndDateConverter.formatTime(j.getArrDateTime()));
        nbrDepTime.setText("restid " + j.getTotalTravelTime() + " min ");
        timeToDep.setText(" om " + j.getTimeToDep());
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


    public static int getDrawableFromLineType(int lineType) {
        switch (lineType) {
            case RouteLink.PENDELN:
                return R.drawable.ic_bus_region;
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
