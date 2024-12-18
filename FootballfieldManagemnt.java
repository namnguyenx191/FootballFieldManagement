package FootballFieldManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.ArrayList;

public class FootballFieldManagement{
    private ArrayList<SanBong> danhSachSan = new ArrayList<>();
    private JPanel panelDanhSachSan;

    private int tenSanCounter = 1;
    private final double[] giaSanTheoLoai = {120000, 300000, 500000, 900000}; // Giá cho loại sân 5, 7, 9, 11

    class SanBong {
        String tenSan, loaiSan;
        double giaSan;
        ArrayList<DatSan> danhSachDatSan = new ArrayList<>();
        boolean daCheckIn = false;

        public SanBong(String tenSan, String loaiSan, double giaSan) {
            this.tenSan = tenSan;
            this.loaiSan = loaiSan;
            this.giaSan = giaSan;
        }

        @Override
        public String toString() {
            return tenSan + " - Loại sân: " + loaiSan + " - " + giaSan + " VND/giờ";
        }
    }

    class DatSan {
        String hoTen;
        LocalTime gioBatDau, gioKetThuc;

        public DatSan(String hoTen, LocalTime gioBatDau, LocalTime gioKetThuc) {
            this.hoTen = hoTen;
            this.gioBatDau = gioBatDau;
            this.gioKetThuc = gioKetThuc;
        }

        public double tinhTien(double giaSan) {
            long gioSuDung = gioKetThuc.getHour() - gioBatDau.getHour();
            return gioSuDung * giaSan;
        }

        public boolean isTimeConflict(LocalTime gioBatDau, LocalTime gioKetThuc) {
            return !(this.gioKetThuc.isBefore(gioBatDau) || this.gioBatDau.isAfter(gioKetThuc));
        }
    }

    public QuanLySanBong() {
        JFrame frame = new JFrame("Quản Lý Sân Bóng");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Trang 1: Thêm sân bóng
        JPanel panelThemSan = new JPanel(new GridLayout(5, 2, 10, 10));
        JComboBox<String> comboLoaiSan = new JComboBox<>(new String[]{"5", "7", "9", "11"});
        JButton btnThemSan = new JButton("Thêm Sân");
        btnThemSan.setPreferredSize(new Dimension(30, 10)); // Kích thước nhỏ hơn cho nút
        panelThemSan.add(new JLabel("Loại sân:"));
        panelThemSan.add(comboLoaiSan);
        panelThemSan.add(new JLabel(""));
        panelThemSan.add(btnThemSan);

        // Thêm sân vào danh sách
        btnThemSan.addActionListener(e -> {
            String loaiSan = (String) comboLoaiSan.getSelectedItem();
            int loaiSanIndex = Integer.parseInt(loaiSan) / 2 - 2; // Loại sân từ 5, 7, 9, 11 -> index 0, 1, 2, 3

            double giaSan = giaSanTheoLoai[loaiSanIndex];
            String tenSan = "Sân " + tenSanCounter++;
            SanBong san = new SanBong(tenSan, loaiSan, giaSan);

            danhSachSan.add(san);
            JOptionPane.showMessageDialog(frame, "Thêm sân thành công!");
            capNhatDanhSachSan();
        });

        // Trang 2: Hiển thị sân bóng
        panelDanhSachSan = new JPanel();
        JScrollPane scrollPane = new JScrollPane(panelDanhSachSan);

        JPanel panelHienThiSan = new JPanel(new BorderLayout());
        panelHienThiSan.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Thêm Sân Bóng", panelThemSan);
        tabbedPane.addTab("Danh Sách Sân Bóng", panelHienThiSan);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private void capNhatDanhSachSan() {
        panelDanhSachSan.removeAll();
        panelDanhSachSan.setLayout(new BoxLayout(panelDanhSachSan, BoxLayout.Y_AXIS)); // Sắp xếp theo chiều dọc

        for (SanBong san : danhSachSan) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Tạo một hàng cho mỗi sân
            rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Tạo khoảng cách giữa các hàng

            JButton btnSan = new JButton("<html>" + san.tenSan + "<br>" + san.loaiSan + "<br>" + san.giaSan + " VND/h</html>");
            btnSan.setBackground(san.daCheckIn ? Color.RED : Color.GREEN);
            btnSan.setOpaque(true);
            btnSan.setBorderPainted(false);
            btnSan.setPreferredSize(new Dimension(150, 60)); // Kích thước cố định cho nút sân
            btnSan.addActionListener(e -> xuLySan(san));
            rowPanel.add(btnSan);

            // Nút sửa
            JButton btnSua = new JButton("Sửa");
            btnSua.setPreferredSize(new Dimension(80, 30));
            btnSua.addActionListener(e -> suaSan(san));
            rowPanel.add(btnSua);

            // Nút xóa
            JButton btnXoa = new JButton("Xóa");
            btnXoa.setPreferredSize(new Dimension(80, 30));
            btnXoa.addActionListener(e -> xoaSan(san));
            rowPanel.add(btnXoa);

            // Nút xem danh sách đặt trước
            JButton btnXemDatTruoc = new JButton("Xem Đặt Trước");
            btnXemDatTruoc.setPreferredSize(new Dimension(120, 30));
            btnXemDatTruoc.addActionListener(e -> xemDanhSachDatTruoc(san));
            rowPanel.add(btnXemDatTruoc);

            panelDanhSachSan.add(rowPanel); // Thêm hàng này vào danh sách
        }

        panelDanhSachSan.revalidate();
        panelDanhSachSan.repaint();
    }

    private void suaSan(SanBong san) {
        String loaiSan = JOptionPane.showInputDialog("Nhập loại sân (5, 7, 9, 11):");
        int loaiSanIndex = Integer.parseInt(loaiSan) / 2 - 2;
        san.loaiSan = loaiSan;
        san.giaSan = giaSanTheoLoai[loaiSanIndex];
        capNhatDanhSachSan();
    }

    private void xoaSan(SanBong san) {
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa sân này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            danhSachSan.remove(san);
            capNhatDanhSachSan();
        }
    }

    private void xemDanhSachDatTruoc(SanBong san) {
        StringBuilder datTruocInfo = new StringBuilder();
        datTruocInfo.append("Danh sách đặt trước cho sân ").append(san.tenSan).append(":\n");
        for (DatSan datSan : san.danhSachDatSan) {
            datTruocInfo.append("Khách hàng: ").append(datSan.hoTen)
                    .append(" | Giờ: ").append(datSan.gioBatDau)
                    .append(" - ").append(datSan.gioKetThuc).append("\n");
        }
        JOptionPane.showMessageDialog(null, datTruocInfo.toString());
    }

    private void xuLySan(SanBong san) {
        if (!san.daCheckIn) {
            String[] options = {"Đặt Sân Trước", "Check In"};
            int choice = JOptionPane.showOptionDialog(null, "Chọn thao tác", "Sân: " + san.tenSan,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) { // Đặt sân trước
                String hoTen = JOptionPane.showInputDialog("Nhập họ tên khách hàng:");
                String gioBD = JOptionPane.showInputDialog("Nhập giờ bắt đầu (0-24):");
                String gioKT = JOptionPane.showInputDialog("Nhập giờ kết thúc (0-24):");
                try {
                    LocalTime gioBatDau = LocalTime.of(Integer.parseInt(gioBD), 0);
                    LocalTime gioKetThuc = LocalTime.of(Integer.parseInt(gioKT), 0);

                    // Kiểm tra trùng giờ
                    for (DatSan datSan : san.danhSachDatSan) {
                        if (datSan.isTimeConflict(gioBatDau, gioKetThuc)) {
                            JOptionPane.showMessageDialog(null, "Khung giờ đã có người đặt.");
                            return;
                        }
                    }

                    san.danhSachDatSan.add(new DatSan(hoTen, gioBatDau, gioKetThuc));
                    JOptionPane.showMessageDialog(null, "Đặt sân trước thành công!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Dữ liệu không hợp lệ!");
                }
            } else if (choice == 1) { // Check In
                String hoTen = JOptionPane.showInputDialog("Nhập họ tên khách hàng:");
                LocalTime gioHienTai = LocalTime.now();
                JOptionPane.showMessageDialog(null, "Check In thành công! Thời gian hiện tại: " + gioHienTai);
                san.daCheckIn = true;
            }
        } else {
            String[] options = {"Check Out"};
            int choice = JOptionPane.showOptionDialog(null, "Chọn thao tác", "Sân: " + san.tenSan,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) { // Check Out
                LocalTime gioHienTai = LocalTime.now();
                long soGioSuDung = 1; // Giả sử mặc định là 1 giờ sử dụng nếu không đặt trước.
                if (!san.danhSachDatSan.isEmpty()) {
                    DatSan datSanGanNhat = san.danhSachDatSan.get(san.danhSachDatSan.size() - 1);
                    soGioSuDung = gioHienTai.getHour() - datSanGanNhat.gioBatDau.getHour();
                }
                double tongTien = soGioSuDung * san.giaSan;
                JOptionPane.showMessageDialog(null, "Check Out thành công! Tổng tiền: " + tongTien + " VND");
                san.daCheckIn = false;
            }
        }
        capNhatDanhSachSan();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QuanLySanBong::new);
    }
}
