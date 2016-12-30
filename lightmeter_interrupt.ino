#include <IntervalTimer.h>
#include <ADC.h>

#define ARRAYSIZE(x) (sizeof(x)/sizeof(x[0]))

const int kSamplesPerSec = 400 * 120;

// Tweak period and use float so we get the exact right number
// of cycles fed into the timer interrupt setup.
// Ideal is 0.5f constant (from IntervalTimer.h), but use 0.6 to be safe.
const float kPeriod = 1000000.0f / kSamplesPerSec + 0.6f / (F_BUS / 1000000);

ADC adc;
IntervalTimer timer;

volatile bool bail = false;

//bool done = false;
volatile int samples = 0;
volatile bool adc_in_progress = false;
elapsedMicros* adc_micros;

// Get results for 3 60Hz periods
unsigned short results[kSamplesPerSec * 3 / 60];

bool started = false;

void setup() {
  Serial.begin(9600);  // baud rate ignored
  // put your setup code here, to run once:
  //delay(1000);
  //Serial.println("setup");
  //Serial.println(kPeriod);
  adc.setAveraging(2);
  adc.setResolution(16);
  adc.setConversionSpeed(ADC_MED_SPEED);
  adc.setSamplingSpeed(ADC_MED_SPEED);
  //adc.enableCompare(1.0/3.3*adc.getMaxValue(ADC_0), 0, ADC_0);
  //adc.enableCompareRange(1.0*adc.getMaxValue(ADC_0)/3.3,
  //                       2.0*adc.getMaxValue(ADC_0)/3.3, 0, 1, ADC_0);
  adc.enableInterrupts(ADC_0);
  //timer.begin(TimerCallback, kPeriod);
}

void loop() {
  if (Serial.available() > 0) {
    int cval = Serial.read();
    if (cval >= 'a' && cval <= 'z') {
      char ret = static_cast<char>(cval) + 1;
      Serial.println(ret);
    } else if (cval == '!' && !started) {
      started = true;
      timer.begin(TimerCallback, kPeriod);
    }
  }
  if (started && (samples == ARRAYSIZE(results) || bail)) {
    started = false;
    timer.end();
    if (bail) {
      memset(results, 0, sizeof(results));
    }
    Serial.write(reinterpret_cast<uint8_t*>(results), ARRAYSIZE(results) * 2);
    samples = 0;
  }
  
  
//  // put your main code here, to run repeatedly:
//  if (bail) {
//    Serial.println("BAIL!");
//    while (true) {}
//  }
//  if (!done && samples >= 20) {
//    done = true;
//    timer.end();
//    Serial.println("got 20 samples");
//    for (int i = 0; i < 20; i++) {
//      Serial.println(results[i]);
//    }
//  }
}

void TimerCallback() {
  if (adc_in_progress) {
    bail = true;
    timer.end();
    return;
  }
  //adc_micros = new elapsedMicros;
  adc.startSingleRead(A6, ADC_0);
}

// must be named exactly adc0_isr
void adc0_isr() {
  if (samples < ARRAYSIZE(results)) {
    //results[samples] = (short)*adc_micros;
    //delete adc_micros;
    results[samples] = adc.readSingle();
    samples++;
  } else {
    adc.readSingle();
  }
  adc_in_progress = false;
}

