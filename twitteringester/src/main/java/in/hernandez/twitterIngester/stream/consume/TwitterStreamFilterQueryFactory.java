package in.hernandez.twitterIngester.stream.consume;

import org.springframework.util.CollectionUtils;
import twitter4j.FilterQuery;

import java.util.List;

public class TwitterStreamFilterQueryFactory {

    public FilterQuery buildQuery(List<String> searchTerms, List<Long> usersIds){
        long[] clonedUsersIds = copyFollows(usersIds);
        String[] clonedSearchTerms = copyTerms(searchTerms);
        return new FilterQuery(0, clonedUsersIds, clonedSearchTerms);
    }

    private String[] copyTerms(List<String> searchTerms) {
        String[] termsArray = null;
        if (!CollectionUtils.isEmpty(searchTerms)) {
            termsArray = searchTerms.toArray(new String[0]);
        }
        return termsArray;
    }

    private long[] copyFollows(List<Long> usersIds) {
        long[] followsArray = null;

        if (!CollectionUtils.isEmpty(usersIds)) {
            followsArray = new long[usersIds.size()];
            for (int i = 0; i < usersIds.size(); i++) {
                followsArray[i] = usersIds.get(i);
            }
        }
        return followsArray;
    }
}
