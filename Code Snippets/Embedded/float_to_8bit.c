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

char *floatToBuffer(float data)
{
	//Gets the binary representation of a float
	union 
	{
	  float    floatData;
	  uint32_t bitData;
	} pun = { .floatData = data };

	//Mask allows us to gather 8 bits at a time
	uint32_t mask = 0xFF000000;
	int i;
	char *hexValArray;

	printf("\nConversion:\ndata inp: %f\t", data);
	printBits(sizeof(data), &data);
	printf("\ndata out: %d\t", pun.bitData);
	printBits(sizeof(pun.bitData), &pun.bitData);
	printf("\n\nMasking bits:\n");

	//Evalutes from MSB to LSB
	for(i = 3; i > -1; i--)
	{
		//Get 8 bits of MSB
		uint32_t val = (pun.bitData & mask);

		//Val is the current HEX char we are evalualting
		val >>= (i*8);
		printf("The val as int: %d\n", val);
		printf("val: %d\t", val);
		printBits(sizeof(val), &val);
		printf("\n");
		mask >>= 8;

		//Store in an array
		hexValArray[3-i] = val;
	}

	//Print everything
	printf("\nResulting hex:\n");
	for(i = 0; i < 4; i++)
		printf("%d, ", hexValArray[i]);

	printf("\n\n");

	return hexValArray;
}

//Array of 8bit characters is input
void charToFloat(char charArray []) {

	int i;
	uint32_t mask = 0x000000FF;

	//MSB to LSB
	for(i = 0; i < 4; i++)
	{
		uint32_t val = (charArray[i] & mask);
		printf("%x, ", val);
	}

}

int main(int argc, char const *argv[])
{
	char *charArray = floatToBuffer(12.3456789);
	charToFloat(charArray);

	return 0;
}