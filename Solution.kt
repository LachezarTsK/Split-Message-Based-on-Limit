
import kotlin.math.min
import kotlin.math.log10

class Solution {

    private class NOT_POSSIBLE {

        companion object {
            val AS_STRING_ARRAY = Array<String>(0) { "" }
            const val AS_NEGATIVE_INTEGER = -1
            const val AS_POSITIVE_INTEGER = Int.MAX_VALUE
        }
    }

    private companion object {
        /*
        Suffix scaffolding is </>
         */
        const val SUFFIX_SCAFFOLDING_SIZE = 3

        /*
        Complete suffix is <index/size>, index is 1-based and min possible size is 1 (when there are no splits)
        Min split size can be one digit. Therefore, suffix must have at least two digits
         */
        const val MIN_NUMBER_OF_DIGITS_IN_SUFFIX = 2
        const val MIN_SUFFIX_SIZE = SUFFIX_SCAFFOLDING_SIZE + MIN_NUMBER_OF_DIGITS_IN_SUFFIX
    }

    fun splitMessage(message: String, limit: Int): Array<String> {
        if (limit <= MIN_SUFFIX_SIZE) {
            return NOT_POSSIBLE.AS_STRING_ARRAY
        }

        val totalSplits = binarySearchForMinNumberValidSplits(message, limit)
        if (totalSplits == NOT_POSSIBLE.AS_POSITIVE_INTEGER) {
            return NOT_POSSIBLE.AS_STRING_ARRAY
        }

        return createSplitMessage(message, limit, totalSplits)
    }

    private fun binarySearchForMinNumberValidSplits(message: String, limit: Int): Int {
        var minSplits = 0
        var maxSplits = message.length - 1
        var totalSplits = NOT_POSSIBLE.AS_POSITIVE_INTEGER

        while (minSplits <= maxSplits) {
            val numberOfSplits = minSplits + (maxSplits - minSplits) / 2

            val numberOfDigitsInTotalSplitParts = (log10(numberOfSplits.toDouble() + 1)).toInt() + 1
            val numberOfInputCharsInLastSplitPart = calculateNumberOfRemainingInputCharsAfterLastSplits(message.length, limit, numberOfSplits, numberOfDigitsInTotalSplitParts)
            val lastSuffixSize = SUFFIX_SCAFFOLDING_SIZE + 2 * numberOfDigitsInTotalSplitParts
            val totalSizeOfLastSplitPart = numberOfInputCharsInLastSplitPart + lastSuffixSize

            if (numberOfInputCharsInLastSplitPart > 0 && totalSizeOfLastSplitPart <= limit) {
                totalSplits = min(numberOfSplits, totalSplits)

                /*
                Reset minSplits and maxSplits to check for valid splits less than the current number of valid splits.
                The number of valid splits must have as few splits as possible.
                 */
                minSplits = 0
                maxSplits = numberOfSplits - 1
            } else if (numberOfInputCharsInLastSplitPart > 0) {
                minSplits = numberOfSplits + 1
            } else {
                maxSplits = numberOfSplits - 1
            }
        }
        return totalSplits
    }

    private fun calculateNumberOfRemainingInputCharsAfterLastSplits(totalInputChars: Int, limit: Int, totalSplits: Int, numberOfDigitsInTotalSplitParts: Int): Int {
        var numberOfDigitsInIndex = 1
        var upperLimitNumberOfDigits = 1

        var remainingSplits = totalSplits
        var remainingInputChars = totalInputChars

        while (remainingSplits > 0) {
            val currentSuffixSize = SUFFIX_SCAFFOLDING_SIZE + numberOfDigitsInIndex + numberOfDigitsInTotalSplitParts
            if (currentSuffixSize >= limit) {
                return NOT_POSSIBLE.AS_NEGATIVE_INTEGER
            }

            val currentNumberOfSplits = min(upperLimitNumberOfDigits * 10 - upperLimitNumberOfDigits, remainingSplits)
            val currentRequiredChars = (limit - currentSuffixSize) * currentNumberOfSplits

            remainingSplits -= currentNumberOfSplits
            remainingInputChars -= currentRequiredChars
            upperLimitNumberOfDigits *= 10
            ++numberOfDigitsInIndex
        }

        return remainingInputChars
    }

    private fun createSplitMessage(message: String, limit: Int, totalSplits: Int): Array<String> {
        var indexResult = 0
        var indexStartSplit = 0
        val numberOfSplitMessages = totalSplits + 1
        val resultSplitMessage = Array<String>(numberOfSplitMessages) { "" }

        for (messageNumber in 1..numberOfSplitMessages) {
            val suffix = "<$messageNumber/$numberOfSplitMessages>"
            val indexEndSplit = min(indexStartSplit + (limit - suffix.length - 1), message.length - 1)
            resultSplitMessage[indexResult] = message.substring(indexStartSplit, indexEndSplit + 1) + suffix
            indexStartSplit = indexEndSplit + 1
            ++indexResult
        }

        return resultSplitMessage
    }
}
