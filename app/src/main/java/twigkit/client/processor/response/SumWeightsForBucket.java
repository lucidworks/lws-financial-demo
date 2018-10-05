package twigkit.client.processor.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twigkit.model.Field;
import twigkit.model.Response;
import twigkit.model.Result;
import twigkit.processor.ResponseProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JohnGUnderwood.
 * Sum weights per industry for companies grouped into buckets on response
 */
public class SumWeightsForBucket extends ResponseProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SumWeightsForBucket.class);

    public static String PARAMETER_WEIGHT_FIELD = "weight_field";
    public static String PARAMETER_SUM_OVER_FIELD = "sum_over_field";


    @Override
    public Response change(Response grouped) {

        List<Result> buckets = grouped.getResults();
        List<Result> newBuckets = new ArrayList<>();

        for(Result r : buckets){
            Map<String,Float> industryWeights = new HashMap<String,Float>();

            List<Result> companies = r.getRelated();
            for(Result c : companies){
                String industry = c.getFields().get(getParameterStringValue(PARAMETER_SUM_OVER_FIELD)).getValue().getActualAsString();
                Float weight = Float.parseFloat(c.getFields().get(getParameterStringValue(PARAMETER_WEIGHT_FIELD)).getValue().getActualAsString());

                if (industryWeights.containsKey(industry)){
                    industryWeights.replace(industry,industryWeights.get(industry)+weight);
                }
                else {
                    industryWeights.put(industry,weight);
                }
            }

            for( Map.Entry<String,Float> e : industryWeights.entrySet()){
                r.addField(new Field(e.getKey(),e.getValue()));
                r.addField(new Field("industries",e.getKey()));
            }

            newBuckets.add(r);
        }

        grouped.setResults(newBuckets);
        return grouped;
    }
}
