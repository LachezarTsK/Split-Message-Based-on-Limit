
public class Solution {

    private static class NOT_POSSIBLE {

        static String[] AS_STRING_ARRAY = {};
        static int AS_NEGATIVE_INTEGER = -1;
        static int AS_POSITIVE_INTEGER = Integer.MAX_VALUE;
    }

    /*
    Suffix scaffolding is </>
     */
    private static final int SUFFIX_SCAFFOLDING_SIZE = 3;

    /*
    Complete suffix is <index/size>, index is 1-based and min possible size is 1 (when there are no splits)
    Min split size can be one digit. Therefore, suffix must have at least two digits 
     */
    private static final int MIN_NUMBER_OF_DIGITS_IN_SUFFIX = 2;
    private static final int MIN_SUFFIX_SIZE = SUFFIX_SCAFFOLDING_SIZE + MIN_NUMBER_OF_DIGITS_IN_SUFFIX;

    public String[] splitMessage(String message, int limit) {
        if (limit <= MIN_SUFFIX_SIZE) {
            return NOT_POSSIBLE.AS_STRING_ARRAY;
        }

        int totalSplits = binarySearchForMinNumberValidSplits(message, limit);
        if (totalSplits == NOT_POSSIBLE.AS_POSITIVE_INTEGER) {
            return NOT_POSSIBLE.AS_STRING_ARRAY;
        }

        return createSplitMessage(message, limit, totalSplits);
    }

    private int binarySearchForMinNumberValidSplits(String message, int limit) {
        int minSplits = 0;
        int maxSplits = message.length() - 1;
        int totalSplits = NOT_POSSIBLE.AS_POSITIVE_INTEGER;

        while (minSplits <= maxSplits) {
            int numberOfSplits = minSplits + (maxSplits - minSplits) / 2;

            int numberOfDigitsInTotalSplitParts = (int) (Math.log10(numberOfSplits + 1)) + 1;
            int numberOfInputCharsInLastSplitPart = calculateNumberOfRemainingInputCharsAfterLastSplits(message.length(), limit, numberOfSplits, numberOfDigitsInTotalSplitParts);
            int lastSuffixSize = SUFFIX_SCAFFOLDING_SIZE + 2 * numberOfDigitsInTotalSplitParts;
            int totalSizeOfLastSplitPart = numberOfInputCharsInLastSplitPart + lastSuffixSize;

            if (numberOfInputCharsInLastSplitPart > 0 && totalSizeOfLastSplitPart <= limit) {
                totalSplits = Math.min(numberOfSplits, totalSplits);

                /*
                Reset minSplits and maxSplits to check for valid splits less than the current number of valid splits.
                The number of valid splits must have as few splits as possible. 
                 */
                minSplits = 0;
                maxSplits = numberOfSplits - 1;
            } else if (numberOfInputCharsInLastSplitPart > 0) {
                minSplits = numberOfSplits + 1;
            } else {
                maxSplits = numberOfSplits - 1;
            }
        }
        return totalSplits;
    }

    private int calculateNumberOfRemainingInputCharsAfterLastSplits(int totalInputChars, int limit, int totalSplits, int numberOfDigitsInTotalSplitParts) {
        int numberOfDigitsInIndex = 1;
        int upperLimitNumberOfDigits = 1;

        int remainingSplits = totalSplits;
        int remainingInputChars = totalInputChars;

        while (remainingSplits > 0) {
            int currentSuffixSize = SUFFIX_SCAFFOLDING_SIZE + numberOfDigitsInIndex + numberOfDigitsInTotalSplitParts;
            if (currentSuffixSize >= limit) {
                return NOT_POSSIBLE.AS_NEGATIVE_INTEGER;
            }

            int currentNumberOfSplits = Math.min(upperLimitNumberOfDigits * 10 - upperLimitNumberOfDigits, remainingSplits);
            int currentRequiredChars = (limit - currentSuffixSize) * currentNumberOfSplits;

            remainingSplits -= currentNumberOfSplits;
            remainingInputChars -= currentRequiredChars;
            upperLimitNumberOfDigits *= 10;
            ++numberOfDigitsInIndex;
        }

        return remainingInputChars;
    }

    private String[] createSplitMessage(String message, int limit, int totalSplits) {
        int indexResult = 0;
        int indexStartSplit = 0;
        int numberOfSplitMessages = totalSplits + 1;
        String[] resultSplitMessage = new String[numberOfSplitMessages];

        for (int messageNumber = 1; messageNumber <= numberOfSplitMessages; ++messageNumber) {
            String suffix = "<" + messageNumber + "/" + numberOfSplitMessages + ">";
            int indexEndSplit = Math.min(indexStartSplit + (limit - suffix.length() - 1), message.length() - 1);
            resultSplitMessage[indexResult] = message.substring(indexStartSplit, indexEndSplit + 1) + suffix;
            indexStartSplit = indexEndSplit + 1;
            ++indexResult;
        }

        return resultSplitMessage;
    }
}
