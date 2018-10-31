package org.lenskit.mooc.cbf;

import org.lenskit.data.ratings.Rating;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build a user profile from all positive ratings.
 */
public class WeightedUserProfileBuilder implements UserProfileBuilder {
    /**
     * The tag model, to get item tag vectors.
     */
    private final TFIDFModel model;

    @Inject
    public WeightedUserProfileBuilder(TFIDFModel m) {
        model = m;
    }

    @Override
    public Map<String, Double> makeUserProfile(@Nonnull List<Rating> ratings) {
        // Create a new vector over tags to accumulate the user profile
        Map<String,Double> profile = new HashMap<>();

        //compute average user rating
        double avgRating = 0.0;
        for (Rating r: ratings) {
            avgRating += r.getValue();
        }
        avgRating /=ratings.size();
        double weight = 0.0;
        for (Rating r: ratings) {
            //  Get this item's vector and add it to the user's profile
            Map<String, Double> tv = model.getItemVector(r.getItemId());
            for (Map.Entry<String, Double> e : tv.entrySet()) {
                weight = (r.getValue()-avgRating)*e.getValue();
                if(profile.containsKey(e.getKey())){
                        profile.put(e.getKey(),weight+profile.get(e.getKey()));
                }
                else{
                        profile.put(e.getKey(),weight);
                }
            }
        }


        // The profile is accumulated, return it.
        return profile;
    }
}
