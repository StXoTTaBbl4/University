import serial
import time
import random

SYNC_BYTE = 0x5A

def crc8(data):
    crc = 0x00
    for byte in data:
        crc ^= byte
        for _ in range(8):
            crc = ((crc << 1) ^ 0x07) if (crc & 0x80) else (crc << 1)
            crc &= 0xFF
    return crc

def make_packet(data, valid=True, error_type=None):
    if not valid:
        if error_type == "no_sync":
            return bytes([0x00] + [len(data)] + data + [crc8(data)])
        elif error_type == "bad_length":
            return bytes([SYNC_BYTE, 0xFF] + data + [crc8(data)])
        elif error_type == "bad_crc":
            return bytes([SYNC_BYTE, len(data)] + data + [crc8(data) ^ 0xFF])
        else:
            raise ValueError("Unknown error_type")
    else:
        return bytes([SYNC_BYTE, len(data)] + data + [crc8(data)])

def parse_packet(packet):
    if len(packet) < 4:
        return "Ошибка: короткий пакет"
    if packet[0] != SYNC_BYTE:
        return "Ошибка: нет синхробайта"
    length = packet[1]
    data = list(packet[2:2+length])
    crc_recv = packet[2+length]
    if crc8(data) != crc_recv:
        return "Ошибка: неверная CRC"
    if length == 2:
        return f"Температура: {data[0]}°C, Влажность: {data[1]}%"
    return f"Принятые данные: {data}"

def main():
    port = "COM3"
    baudrate = 57600
    timeout = 1

    with serial.Serial(port, baudrate=baudrate, parity=serial.PARITY_EVEN, stopbits=1, timeout=timeout) as ser:
        print(f"Открыт порт {port} @ {baudrate} 8E1")

        try:
            while True:
                # Пример: случайно отправляем один из вариантов
                mode = random.choice(["valid", "no_sync", "bad_length", "bad_crc"])
                mode = "valid"
                if mode == "valid":
                    data = [ord('T'), ord('E'), ord('S'), ord('T')]
                    packet = make_packet(data, valid=True)
                    print("➡ Отправка: корректный пакет")
                else:
                    packet = make_packet([1, 2, 3], valid=False, error_type=mode)
                    print(f"➡ Отправка: ошибка '{mode}'")

                ser.write(packet)
                time.sleep(0.2)

                response = ser.read(64)
                if response:
                    print("⬅ Ответ:", parse_packet(list(response)))
                else:
                    print("❌ Нет ответа")

                print("-" * 40)
                time.sleep(2)

        except KeyboardInterrupt:
            print("Завершено.")




# def main():
#     port = "COM3"
#     baudrate = 57600
#     with serial.Serial(port, baudrate=baudrate, parity=serial.PARITY_EVEN, stopbits=1, timeout=1) as ser:
#         print(f"Слушаем {port} @ {baudrate} 8E1...")
#         try:
#             while True:
#                 data = ser.read(64)  # читаем до 64 байт (или меньше, если timeout)
#                 if data:
#                     print("Получено:", list(data))
#         except KeyboardInterrupt:
#             print("Завершено.")


if __name__ == "__main__":
    main()
