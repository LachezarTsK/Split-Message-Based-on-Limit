
/**
 * @param {string} message
 * @param {number} limit
 * @return {string[]}
 */
var splitMessage = function (message, limit) {
    if (limit <= Util.MIN_SUFFIX_SIZE) {
        return NOT_POSSIBLE.AS_STRING_ARRAY;
    }

    const totalSplits = binarySearchForMinNumberValidSplits(message, limit);
    if (totalSplits === NOT_POSSIBLE.AS_POSITIVE_INTEGER) {
        return NOT_POSSIBLE.AS_STRING_ARRAY;
    }

    return createSplitMessage(message, limit, totalSplits);
};

class NOT_POSSIBLE {

    static AS_STRING_ARRAY = new Array();
    static AS_NEGATIVE_INTEGER = -1;
    static AS_POSITIVE_INTEGER = Number.MAX_SAFE_INTEGER;
}

class Util {
    /*
     Suffix scaffolding is </>
     */
    static SUFFIX_SCAFFOLDING_SIZE = 3;

    /*
     Complete suffix is <index/size>, index is 1-based and min possible size is 1 (when there are no splits)
     Min split size can be one digit. Therefore, suffix must have at least two digits 
     */
    static MIN_NUMBER_OF_DIGITS_IN_SUFFIX = 2;
    static MIN_SUFFIX_SIZE = Util.SUFFIX_SCAFFOLDING_SIZE + Util.MIN_NUMBER_OF_DIGITS_IN_SUFFIX;
}

/**
 * @param {string} message
 * @param {number} limit
 * @return {number}
 */
function binarySearchForMinNumberValidSplits(message, limit) {
    let minSplits = 0;
    let maxSplits = message.length - 1;
    let totalSplits = NOT_POSSIBLE.AS_POSITIVE_INTEGER;

    while (minSplits <= maxSplits) {
        const numberOfSplits = minSplits + Math.floor((maxSplits - minSplits) / 2);

        const numberOfDigitsInTotalSplitParts = Math.floor(Math.log10(numberOfSplits + 1)) + 1;
        const numberOfInputCharsInLastSplitPart = calculateNumberOfRemainingInputCharsAfterLastSplits(message.length, limit, numberOfSplits, numberOfDigitsInTotalSplitParts);
        const lastSuffixSize = Util.SUFFIX_SCAFFOLDING_SIZE + 2 * numberOfDigitsInTotalSplitParts;
        const totalSizeOfLastSplitPart = numberOfInputCharsInLastSplitPart + lastSuffixSize;

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

/**
 * @param {number} totalInputChars
 * @param {number} limit
 * @param {number} totalSplits
 * @param {number} numberOfDigitsInTotalSplitParts
 * @return {number}
 */
function calculateNumberOfRemainingInputCharsAfterLastSplits(totalInputChars, limit, totalSplits, numberOfDigitsInTotalSplitParts) {
    let numberOfDigitsInIndex = 1;
    let upperLimitNumberOfDigits = 1;

    let remainingSplits = totalSplits;
    let remainingInputChars = totalInputChars;

    while (remainingSplits > 0) {
        const currentSuffixSize = Util.SUFFIX_SCAFFOLDING_SIZE + numberOfDigitsInIndex + numberOfDigitsInTotalSplitParts;
        if (currentSuffixSize >= limit) {
            return NOT_POSSIBLE.AS_NEGATIVE_INTEGER;
        }

        const currentNumberOfSplits = Math.min(upperLimitNumberOfDigits * 10 - upperLimitNumberOfDigits, remainingSplits);
        const currentRequiredChars = (limit - currentSuffixSize) * currentNumberOfSplits;

        remainingSplits -= currentNumberOfSplits;
        remainingInputChars -= currentRequiredChars;
        upperLimitNumberOfDigits *= 10;
        ++numberOfDigitsInIndex;
    }

    return remainingInputChars;
}

/**
 * @param {string} message
 * @param {number} limit
 * @param {number} totalSplits
 * @return {string[]}
 */
function createSplitMessage(message, limit, totalSplits) {
    let indexResult = 0;
    let indexStartSplit = 0;
    let numberOfSplitMessages = totalSplits + 1;
    const resultSplitMessage = new Array(numberOfSplitMessages);

    for (let messageNumber = 1; messageNumber <= numberOfSplitMessages; ++messageNumber) {
        const suffix = '<' + messageNumber + '/' + numberOfSplitMessages + '>';
        const indexEndSplit = Math.min(indexStartSplit + (limit - suffix.length - 1), message.length - 1);
        resultSplitMessage[indexResult] = message.substring(indexStartSplit, indexEndSplit + 1) + suffix;
        indexStartSplit = indexEndSplit + 1;
        ++indexResult;
    }

    return resultSplitMessage;
}
