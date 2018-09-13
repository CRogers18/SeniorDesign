#include <stdio.h>
#include <stdint.h>

// Taken from StackOverflow
void printBits(size_t const size, void const * const ptr)
{
    unsigned char *b = (unsigned char*) ptr;
    unsigned char byte;
    int i, j;

    for (i=size-1;i>=0;i--)
    {
        for (j=7;j>=0;j--)
        {
            byte = (b[i] >> j) & 1;
            printf("%u", byte);
        }
    }
}

void floatToBuffer(float data)
{
	//Gets the binary representation of a float
	union 
	{
	  float    floatData;
	  uint32_t bitData;
	} pun = { .floatData = data };

	//Mask allows us to gather 8 bits at a time
	uint32_t mask = 0xFF000000;
	uint32_t val;
	int i;
	uint8_t hexValArray[4] = {0};

	printf("\nConversion:\ndata inp: %f\n", data);
	printBits(sizeof(data), &data);
	printf("\n\ndata out: %d\n", pun.bitData);
	printBits(sizeof(pun.bitData), &pun.bitData);

	//Evalutes from MSB to LSB
	for(i = 3; i > -1; i--)
	{
		// Get 8 bits of MSB
		uint32_t val = (pun.bitData & mask);

		val >>= (i*8);

		// printBits(sizeof(val), &val);
		mask >>= 8;
		// Send to transmit buffer here
		hexValArray[3-i] = (uint8_t) val;
	}

	/*
	printf("\n\nOutput:\n");
	for(i = 0; i < 4; i++)
	{
		printBits(sizeof(uint8_t), &hexValArray[i]);
	}
	*/
	
	// Rebuilt unsigned 32-bit value from unsigned 8-bit values (TODO IN APP)
	val = (hexValArray[3]) | (hexValArray[2] << 8) | (hexValArray[1] << 16) | (hexValArray[0] << 24);

	union 
	{
	  float    floatVal;
	  uint32_t valData;
	} pun2 = { .valData = val };

	printf("\n\nValue Rebuilt:\n%f\n", pun2.floatVal);

}

int main(int argc, char const *argv[])
{
	floatToBuffer(12.3456789);

	return 0;
}