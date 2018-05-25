import importlib
import sys

numpy_lib = importlib.util.find_spec("numpy")
found_numpy = numpy_lib is not None
if found_numpy:
    print("Numpy is installed!")
else:
    print("Numpy is required to run this program!")
    sys.exit(1)

import numpy as np

matplot_lib = importlib.util.find_spec("matplotlib")
found_matplotlib = matplot_lib is not None
if found_matplotlib:
    print("matplotlib is installed!")
    import matplotlib.pyplot as plt

# This is what we want to recognise
_character_match = 'A'

# Sigmoid


def nonlin(result, deriv=False):
    if(deriv):
        return result * (1 - result)
    return 1 / (1 + np.exp(-result))


def relu(result, deriv=False):
    if (deriv):
        result[result <= 0] = 0
        result[result > 0] = 1
        return result
    return np.maximum(result, 0)


def readData():
    with open("letter-recognition.data", "r") as f:
        content = f.readlines()

    data = []

    num_lines = 0

    for line in content:
        num_lines = num_lines + 1
        _char = line.split(',')
        data.append(
            Character(
                _char[0].strip(),
                _char[1].strip(),
                _char[2].strip(),
                _char[3].strip(),
                _char[4].strip(),
                _char[5].strip(),
                _char[6].strip(),
                _char[7].strip(),
                _char[8].strip(),
                _char[9].strip(),
                _char[10].strip(),
                _char[11].strip(),
                _char[12].strip(),
                _char[13].strip(),
                _char[14].strip(),
                _char[15].strip(),
                _char[16].strip()))

    print("Read: " + str(num_lines) + " # of lines")
    return data


# Preprocessing
class Character:
    # 1.	lettr	capital letter	(26 values from A to Z)
    # 2.	x-box	horizontal position of box	(integer)
    # 3.	y-box	vertical position of box	(integer)
    # 4.	width	width of box			(integer)
    # 5.	high 	height of box			(integer)
    # 6.	onpix	total # on pixels		(integer)
    # 7.	x-bar	mean x of on pixels in box	(integer)
    # 8.	y-bar	mean y of on pixels in box	(integer)
    # 9.	x2bar	mean x variance			(integer)
    # 10.	y2bar	mean y variance			(integer)
    # 11.	xybar	mean x y correlation		(integer)
    # 12.	x2ybr	mean of x * x * y		(integer)
    # 13.	xy2br	mean of x * y * y		(integer)
    # 14.	x-ege	mean edge count left to right	(integer)
    # 15.	xegvy	correlation of x-ege with y	(integer)
    # 16.	y-ege	mean edge count bottom to top	(integer)
    # 17.	yegvx	correlation of y-ege with x	(integer)
    def __init__(
            self,
            _lettr,
            _x_box,
            _y_box,
            _width,
            _high,
            _onpix,
            _x_bar,
            _y_bar,
            _x2bar,
            _y2bar,
            _xybar,
            _x2ybr,
            _xy2br,
            _x_ege,
            _xegvy,
            _y_ege,
            _yegvx):
        self.lettr = _lettr.strip()
        self.x_box = self._scale(int(_x_box))
        self.y_box = self._scale(int(_y_box))
        self.width = self._scale(int(_width))
        self.high = self._scale(int(_high))
        self.onpix = self._scale(int(_onpix))
        self.x_bar = self._scale(int(_x_bar))
        self.y_bar = self._scale(int(_y_bar))
        self.x2bar = self._scale(int(_x2bar))
        self.y2bar = self._scale(int(_y2bar))
        self.xybar = self._scale(int(_xybar))
        self.x2ybr = self._scale(int(_x2ybr))
        self.xy2br = self._scale(int(_xy2br))
        self.x_ege = self._scale(int(_x_ege))
        self.xegvy = self._scale(int(_xegvy))
        self.y_ege = self._scale(int(_y_ege))
        self.yegvx = self._scale(int(_yegvx))

    def _scale(self, num):
        new_min = 0
        new_max = 1

        new_value = ((num - 0) / (15 - 0)) * (new_max - new_min) + new_min
        return new_value

    def _zscore(self, a, axis=0, ddof=0):
        a = np.asanyarray(a)
        mns = a.mean(axis=axis)
        sstd = a.std(axis=axis, ddof=ddof)
        if axis and mns.ndim < a.ndim:
            return ((a - np.expand_dims(mns, axis=axis)) /
                    np.expand_dims(sstd, axis=axis))
        else:
            return (a - mns) / sstd

    def getNPDataArray(self):
        return np.array([[self.x_box,
                          self.y_box,
                          self.width,
                          self.high,
                          self.onpix,
                          self.x_bar,
                          self.y_bar,
                          self.x2bar,
                          self.y2bar,
                          self.xybar,
                          self.x2ybr,
                          self.xy2br,
                          self.x_ege,
                          self.xegvy,
                          self.y_ege,
                          self.yegvx,
                          1]])  # Bias unit

    def getNPAnswerArray(self):
        # Char
        arr = np.array([[0]])
        # print(_character_match)
        if self.lettr in _character_match:
            arr[0][0] = 1
        return arr


class NeuralNetwork:
    def __init__(
            self,
            training_input,
            training_answers,
            num_epochs=100,
            learning_rate=0.9,
            hidden_units=150,
            momentum=0.01):
        self.num_epochs = num_epochs
        self.learning_rate = learning_rate
        self.hidden_units = hidden_units
        self.momentum = momentum

        self.training_success_percentage = 0
        self.training_error = 0
        self.validation_success_percentage = 0

        self.stats_error = []
        self.stats_success = []

        self.stats_error_epoch = []
        self.stats_success_epoch = []

        np.random.seed(1)
        # Random weights
        # 1/sqrt(fanin) seems to perform worse than [0,1] normal distribution
        #self.syn0 = (1/np.sqrt(hidden_units)) * np.random.random((training_input.shape[1], hidden_units)) - (1/np.sqrt(hidden_units))
        self.syn0 = 2 * \
            np.random.random((training_input.shape[1], hidden_units)) - 1
        self.syn1 = 2 * \
            np.random.random((hidden_units, training_answers.shape[1])) - 1

        self.training_input_values = training_input
        self.training_answers = training_answers

    def _unison_shuffled_copies(self, a, b):
        assert len(a) == len(b)
        p = np.random.permutation(len(a))
        return a[p], b[p]

    def _convertToChar(self, arr, expectedarr):
        result = 0
        if arr > 0.7:
            result = 1
        expected = self.training_answers[0]
        if result != expected:
            print("Got vowel expected consonant")

    def validate(self, input_values, answer_values):
        print("Validation: ")

        correct = 0
        total = 0

        for j in range(input_values.shape[0]):
            if (j >= input_values.shape[0]):
                break
            l0 = input_values[j]
            l1 = nonlin(np.dot(l0, self.syn0))
            l2 = nonlin(np.dot(l1, self.syn1))

            result = 0
            if l2 >= 0.7:
                result = 1
            expected = answer_values[j]

            if j == 4000:
                print("Got: " + chr(result + 97).upper() +
                      " Expected: " + chr(expected + 97).upper())

            if result == expected:
                correct = correct + 1
            total = total + 1

            #self._convertToChar(l2, answer_values[j])

            if (j % 100) == 0:

                print("Validate[" + str(total) + "] - percentage correct: " +
                      str(np.round(correct / total * 100, decimals=3)) + "% " +
                      str(correct) +
                      "/" +
                      str(total))
        self.validation_success_percentage = np.round(
            correct / total * 100, decimals=3)

    def printStats(self):
        print("======================================")
        print("Network stats: ")
        print("Learning rate: " + str(self.learning_rate))
        print("Momentum: " + str(self.momentum))
        print("Epochs: " + str(self.num_epochs))
        print("Hidden units: " + str(self.hidden_units))
        print("Training error: " + str(self.training_error))
        print("Training success: " + str(self.training_success_percentage) + "%")
        print("Validation success: " +
              str(self.validation_success_percentage) + "%")
        print("======================================")

    def train(self):
        for j in range(self.num_epochs):
            # Shuffle training data.
            self.training_input_values, self.training_answers = self._unison_shuffled_copies(
                self.training_input_values, self.training_answers)
            correct = 0
            total = 0
            print("Epoch: " + str(j))

            prev_l1_delta = 0
            prev_l2_delta = 0

            for k in range(self.training_input_values.shape[0]):
                # Feed forward through layers 0, 1, and 2
                # Input layer
                l0 = self.training_input_values[k * 1: k * 1 + 1]
                # Hidden layer
                l1 = nonlin(np.dot(l0, self.syn0))
                # Output layer
                l2 = nonlin(np.dot(l1, self.syn1))

                # how much did we miss the target value?
                l2_error = self.training_answers[k * 1: k * 1 + 1] - l2

                # in what direction is the target value
                # were we really sure? if so, don't change too much.
                l2_delta = l2_error * \
                    nonlin(l2, deriv=True) * self.learning_rate

                self.training_error = np.mean(np.abs(l2_delta))

                # how much did each l1 value contribute to the l2 error
                # (according to the weights)
                l1_error = l2_delta.dot(self.syn1.T)

                # in what direction is the target l1
                # were we really sure? if so, don't change too much.
                l1_delta = l1_error * \
                    nonlin(l1, deriv=True) * self.learning_rate

                # Back propogate
                self.syn1 += (l1.T.dot(l2_delta)) + \
                    (self.momentum * prev_l2_delta)
                self.syn0 += (l0.T.dot(l1_delta)) + \
                    (self.momentum * prev_l1_delta)

                prev_l1_delta = l1_delta
                prev_l2_delta = l2_delta

                result = 0
                if l2 >= 0.7:
                    result = 1
                expected = self.training_answers[k * 1: k * 1 + 1][0]

                if result == expected:
                    correct = correct + 1
                total = total + 1

                if (k % 1000) == 0:
                    print("Epoch[" +
                          str(j) +
                          "] - percentage correct: " +
                          str(np.round(correct /
                                       total *
                                       100, decimals=3)) +
                          "% " +
                          str(correct) +
                          "/" +
                          str(total))

                    self.stats_error.append(self.training_error)

                    if (k != 0):
                        self.stats_success.append(np.round(correct /
                                                           total *
                                                           100, decimals=3))

            self.training_success_percentage = np.round(correct /
                                                        total *
                                                        100, decimals=3)

            if np.round(correct / total * 100) == 100:
                break

            self.stats_error_epoch.append(self.training_error)
            self.stats_success_epoch.append(self.training_success_percentage)


def tests():
    if not matplot_lib:
        print("Matplotlib is required to run tests")
        return

    # Tests
    epochs = 30
    hidden_units = 100

    # Character to use

    best_success = -1
    best_nn = None

    # Experiments
    # Learning rate
    for x in range(100):
        #momentum = x / 10
        momentum = 0.01
        learning_rate = x / 10
        if learning_rate == 1:
            break
        #learning_rate = 0.9

        nn = NeuralNetwork(
            training_input_values,
            training_answer_values,
            num_epochs=epochs,
            hidden_units=hidden_units,
            momentum=momentum,
            learning_rate=learning_rate)
        nn.train()
        nn.validate(test_input, test_answers)
        nn.printStats()

        a = plt.subplot(2, 1, 1)
        plt.plot(nn.stats_success, '.-', label=str(hidden_units))
        plt.ylabel('Percentage success')
        plt.title('Epochs: ' +
                  str(nn.num_epochs) +
                  ' Hidden units: ' +
                  str(nn.hidden_units) +
                  ' Momentum: ' +
                  str(nn.momentum) +
                  ' LearningR: ' +
                  str(nn.learning_rate))
        plt.ylim([0, 100])
        plt.legend(loc=4)

        y_mean = [np.mean(nn.stats_error)] * len(nn.stats_error)

        b = plt.subplot(2, 1, 2)
        plt.plot(nn.stats_error, '-', label=str(hidden_units))
        plt.plot(y_mean, 'g', linestyle='--')
        plt.ylabel('Error rate')
        plt.xlabel('Training iterations in thousands')

        plt.legend()

        plt.savefig(
            'E1_NN_Training_Momentum_' +
            str(momentum) +
            '_' +
            str(epochs) +
            'Epochs_' +
            str(hidden_units) +
            '_LR_' +
            str(learning_rate) +
            '_Hiddenunits.png')
        plt.clf()

        a = plt.subplot(2, 1, 1)
        plt.plot(nn.stats_success_epoch, '.-', label=str(hidden_units))
        plt.ylabel('Percentage success')
        plt.title('Epochs: ' +
                  str(nn.num_epochs) +
                  ' Hidden units: ' +
                  str(nn.hidden_units) +
                  ' Momentum: ' +
                  str(nn.momentum) +
                  ' LearningR: ' +
                  str(nn.learning_rate))
        plt.ylim([0, 100])
        plt.legend(loc=4)

        y_mean = [np.mean(nn.stats_error_epoch)] * len(nn.stats_error_epoch)

        b = plt.subplot(2, 1, 2)
        plt.plot(nn.stats_error_epoch, '-', label=str(hidden_units))
        plt.plot(y_mean, 'g', linestyle='--')
        plt.ylabel('Error rate')
        plt.xlabel('Epochs')

        plt.legend()

        plt.savefig(
            'E1_NN_Epoch_Momentum_' +
            str(momentum) +
            '_' +
            str(epochs) +
            'Epochs_' +
            str(hidden_units) +
            '_LR_' +
            str(learning_rate) +
            '_Hiddenunits.png')
        plt.clf()

        if nn.training_success_percentage > best_success:
            best_success = nn.training_success_percentage
            best_nn = nn
            print("New best")
        else:
            print("This NN is still best:")
            best_nn.printStats()

    # Momentum
    for x in range(100):
        momentum = x / 10
        learning_rate = 0.9
        if momentum == 1:
            break

        nn = NeuralNetwork(
            training_input_values,
            training_answer_values,
            num_epochs=epochs,
            hidden_units=hidden_units,
            momentum=momentum,
            learning_rate=learning_rate)
        nn.train()
        nn.validate(test_input, test_answers)
        nn.printStats()

        a = plt.subplot(2, 1, 1)
        plt.plot(nn.stats_success, '.-', label=str(hidden_units))
        plt.ylabel('Percentage success')
        plt.title('Epochs: ' +
                  str(nn.num_epochs) +
                  ' Hidden units: ' +
                  str(nn.hidden_units) +
                  ' Momentum: ' +
                  str(nn.momentum) +
                  ' LearningR: ' +
                  str(nn.learning_rate))
        plt.ylim([0, 100])
        plt.legend(loc=4)

        y_mean = [np.mean(nn.stats_error)] * len(nn.stats_error)

        b = plt.subplot(2, 1, 2)
        plt.plot(nn.stats_error, '-', label=str(hidden_units))
        plt.plot(y_mean, 'g', linestyle='--')
        plt.ylabel('Error rate')
        plt.xlabel('Training iterations in thousands')

        plt.legend()

        plt.savefig(
            'E1_NN_Training_Momentum_' +
            str(momentum) +
            '_' +
            str(epochs) +
            'Epochs_' +
            str(hidden_units) +
            '_LR_' +
            str(learning_rate) +
            '_Hiddenunits.png')
        plt.clf()

        a = plt.subplot(2, 1, 1)
        plt.plot(nn.stats_success_epoch, '.-', label=str(hidden_units))
        plt.ylabel('Percentage success')
        plt.title('Epochs: ' +
                  str(nn.num_epochs) +
                  ' Hidden units: ' +
                  str(nn.hidden_units) +
                  ' Momentum: ' +
                  str(nn.momentum) +
                  ' LearningR: ' +
                  str(nn.learning_rate))
        plt.ylim([0, 100])
        plt.legend(loc=4)

        y_mean = [np.mean(nn.stats_error_epoch)] * len(nn.stats_error_epoch)

        b = plt.subplot(2, 1, 2)
        plt.plot(nn.stats_error_epoch, '-', label=str(hidden_units))
        plt.plot(y_mean, 'g', linestyle='--')
        plt.ylabel('Error rate')
        plt.xlabel('Epochs')

        plt.legend()

        plt.savefig(
            'E1_NN_Epoch_Momentum_' +
            str(momentum) +
            '_' +
            str(epochs) +
            'Epochs_' +
            str(hidden_units) +
            '_LR_' +
            str(learning_rate) +
            '_Hiddenunits.png')
        plt.clf()

        if nn.training_success_percentage > best_success:
            best_success = nn.training_success_percentage
            best_nn = nn
            print("New best")
        else:
            print("This NN is still best:")
            best_nn.printStats()


if __name__ == "__main__":
    _character_match = input("Enter character to train on: ")

    characters = readData()

    training_input_values = np.array(characters[0].getNPDataArray())
    training_answer_values = np.array(characters[0].getNPAnswerArray())

    # How much training data do we want?
    limit = 18000
    _count = 1

    # Training set
    for character in characters:
        _count = _count + 1
        training_input_values = np.append(
            training_input_values,
            character.getNPDataArray(),
            axis=0)
        training_answer_values = np.append(
            training_answer_values,
            character.getNPAnswerArray(),
            axis=0)
        if _count >= limit:
            break

    # Validation set
    test_input = np.array(characters[limit].getNPDataArray())
    test_answers = np.array(characters[limit].getNPAnswerArray())

    for i in range(limit, 20000):
        test_input = np.append(
            test_input,
            characters[i].getNPDataArray(),
            axis=0)
        test_answers = np.append(
            test_answers,
            characters[i].getNPAnswerArray(),
            axis=0)

    print("Input values matrix: " + str(training_input_values.shape))
    print("Answers values matrix:" + str(training_answer_values.shape))
    print("Starting...")

    # tests
    tests()
