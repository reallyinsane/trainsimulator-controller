package io.mathan.trainsimulator.ft232h;

public enum Pin {
    C0((byte) 0x01, true),
    C1((byte) 0x02, true),
    C2((byte) 0x04, true),
    C3((byte) 0x08, true),
    C4((byte) 0x10, true),
    C5((byte) 0x20, true),
    C6((byte) 0x40, true),
    C7((byte) 0x80, true),
    D0((byte) 0x01, false),
    D1((byte) 0x02, false),
    D2((byte) 0x04, false),
    D3((byte) 0x08, false),
    D4((byte) 0x10, false),
    D5((byte) 0x20, false),
    D6((byte) 0x40, false),
    D7((byte) 0x80, false);
    private final byte address;
    private final boolean high;

    Pin(byte address, boolean high) {
      this.address = address;
      this.high = high;
    }

    public boolean isHigh() {
      return high;
    }

    public byte getAddress() {
      return address;
    }
  }