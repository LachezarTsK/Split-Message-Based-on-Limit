
package main

import (
    "math"
    "strconv"
)

type Util struct {
    AS_STRING_ARRAY     []string
    AS_NEGATIVE_INTEGER int
    AS_POSITIVE_INTEGER int
}

var NOT_POSSIBLE = Util{
    AS_NEGATIVE_INTEGER: -1,
    AS_POSITIVE_INTEGER: math.MaxInt,
}

/*
Suffix scaffolding is </>
*/
const SUFFIX_SCAFFOLDING_SIZE = 3

/*
Complete suffix is <index/size>, index is 1-based and min possible size is 1 (when there are no splits)
Min split size can be one digit. Therefore, suffix must have at least two digits
*/
const MIN_NUMBER_OF_DIGITS_IN_SUFFIX = 2
const MIN_SUFFIX_SIZE = SUFFIX_SCAFFOLDING_SIZE + MIN_NUMBER_OF_DIGITS_IN_SUFFIX

func splitMessage(message string, limit int) []string {
    if limit <= MIN_SUFFIX_SIZE {
        return NOT_POSSIBLE.AS_STRING_ARRAY
    }

    totalSplits := binarySearchForMinNumberValidSplits(message, limit)
    if totalSplits == NOT_POSSIBLE.AS_POSITIVE_INTEGER {
        return NOT_POSSIBLE.AS_STRING_ARRAY
    }

    return createSplitMessage(message, limit, totalSplits)
}

func binarySearchForMinNumberValidSplits(message string, limit int) int {
    minSplits := 0
    maxSplits := len(message) - 1
    totalSplits := NOT_POSSIBLE.AS_POSITIVE_INTEGER

    for minSplits <= maxSplits {
        numberOfSplits := minSplits + (maxSplits - minSplits) / 2

        numberOfDigitsInTotalSplitParts := int(math.Log10(float64(numberOfSplits) + 1)) + 1
        numberOfInputCharsInLastSplitPart := calculateNumberOfRemainingInputCharsAfterLastSplits(len(message), limit, numberOfSplits, numberOfDigitsInTotalSplitParts)
        lastSuffixSize := SUFFIX_SCAFFOLDING_SIZE + 2 * numberOfDigitsInTotalSplitParts
        totalSizeOfLastSplitPart := numberOfInputCharsInLastSplitPart + lastSuffixSize

        if numberOfInputCharsInLastSplitPart > 0 && totalSizeOfLastSplitPart <= limit {
            totalSplits = min(numberOfSplits, totalSplits)

            /*
               Reset minSplits and maxSplits to check for valid splits less than the current number of valid splits.
               The number of valid splits must have as few splits as possible.
            */
            minSplits = 0
            maxSplits = numberOfSplits - 1
        } else if numberOfInputCharsInLastSplitPart > 0 {
            minSplits = numberOfSplits + 1
        } else {
            maxSplits = numberOfSplits - 1
        }
    }
    return totalSplits
}

func calculateNumberOfRemainingInputCharsAfterLastSplits(totalInputChars int, limit int, totalSplits int, numberOfDigitsInTotalSplitParts int) int {
    numberOfDigitsInIndex := 1
    upperLimitNumberOfDigits := 1

    remainingSplits := totalSplits
    remainingInputChars := totalInputChars

    for remainingSplits > 0 {
        currentSuffixSize := SUFFIX_SCAFFOLDING_SIZE + numberOfDigitsInIndex + numberOfDigitsInTotalSplitParts
        if currentSuffixSize >= limit {
            return NOT_POSSIBLE.AS_NEGATIVE_INTEGER
        }

        currentNumberOfSplits := min(upperLimitNumberOfDigits * 10 - upperLimitNumberOfDigits, remainingSplits)
        currentRequiredChars := (limit - currentSuffixSize) * currentNumberOfSplits

        remainingSplits -= currentNumberOfSplits
        remainingInputChars -= currentRequiredChars
        upperLimitNumberOfDigits *= 10
        numberOfDigitsInIndex++
    }

    return remainingInputChars
}

func createSplitMessage(message string, limit int, totalSplits int) []string {
    indexResult := 0
    indexStartSplit := 0
    numberOfSplitMessages := totalSplits + 1
    resultSplitMessage := make([]string, numberOfSplitMessages)

    for messageNumber := 1; messageNumber <= numberOfSplitMessages; messageNumber++ {
        suffix := "<" + strconv.Itoa(messageNumber) + "/" + strconv.Itoa(numberOfSplitMessages) + ">"
        indexEndSplit := min(indexStartSplit + (limit - len(suffix) - 1), len(message) - 1)
        resultSplitMessage[indexResult] = message[indexStartSplit:indexEndSplit + 1] + suffix
        indexStartSplit = indexEndSplit + 1
        indexResult++
    }

    return resultSplitMessage
}
