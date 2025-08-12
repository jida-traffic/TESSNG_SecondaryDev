package TESS_Java_APIDemo.SecondaryDevCasesMaster;

public class TessApiExample {
        private SecondaryDevelopmentCases secondaryDev;
        private int planNumber;

        // 构造方法，初始化secondaryDev和planNumber
        public TessApiExample() {
            this.secondaryDev = new SecondaryDevelopmentCases(3);
            this.planNumber = 0;
        }

        /**
         * 测试双环信控方案下发
         */
        public void isOk() {
            // 测试双环信控方案下发
//            secondaryDev.doubleRingSignalControlTest(planNumber);
            planNumber++;
            // JOptionPane.showMessageDialog(null, "is ok!");
        }

        /**
         * 编辑相位示例
         */
        public void editPhaseExample() {
            secondaryDev.controlMeasures(3);
        }

        /**
         * 更新路段限速
         */
        public void updateLinkLimitSpeed() {
            secondaryDev.controlMeasures(5);
        }

        /**
         * 启动仿真
         */
        public void startSimulation() {
            secondaryDev.processControl(1);
        }

        /**
         * 暂停仿真
         */
        public void pauseSimulation() {
            secondaryDev.processControl(2);
        }

        /**
         * 停止仿真
         */
        public void stopSimulation() {
            secondaryDev.processControl(3);
        }

        /**
         * 恢复仿真
         */
        public void restoreSimulation() {
            secondaryDev.processControl(4);
        }

        /**
         * 生成快照
         */
        public void generateSnapshot() {
            secondaryDev.processControl(5);
        }

        /**
         * 查找路段或车道中的车辆信息
         */
        public void findVehiInLinkOrLaneInformation() {
            secondaryDev.processControl(8.2);
        }

        /**
         * 查找车辆信息
         */
        public void findVehicleInformation() {
            secondaryDev.processControl(8.3);
        }

        /**
         * 设置仿真精度
         */
        public void setSimulationAccuracy() {
            secondaryDev.processControl(10);
        }

        /**
         * 设置仿真时间间隔
         */
        public void setSimulationInterval() {
            secondaryDev.processControl(11);
        }

        /**
         * 设置仿真加速倍数
         */
        public void setSimulationAcceMultiples() {
            secondaryDev.processControl(12);
        }

        // 动作控制相关方法

        /**
         * 动态发车参数计算示例
         */
        public void calcDynaDispatchParametersExample() {
            secondaryDev.actionControl(1);
        }

        /**
         * 车辆移动示例
         */
        public void moveVehiExample() {
            secondaryDev.actionControl(2);
        }

        /**
         * 设置车辆速度示例
         */
        public void setVehiSpeedExample() {
            secondaryDev.actionControl(3);
        }

        /**
         * 设置车辆路径示例
         */
        public void setVehiRoutingExample() {
            secondaryDev.actionControl(4);
        }

        /**
         * 强制车辆不变道示例
         */
        public void forceVehiDontChangeLaneExample() {
            secondaryDev.actionControl(5);
        }

        /**
         * 强制车辆变道示例
         */
        public void forceVehiChangeLaneExample() {
            secondaryDev.actionControl(6);
        }

        /**
         * 闯红灯示例
         */
        public void runRedLightExample() {
            secondaryDev.actionControl(7);
        }

        /**
         * 强制车辆停车示例
         */
        public void stopVehiExample() {
            secondaryDev.actionControl(8);
        }

        /**
         * 设置车辆角度示例
         */
        public void setVehiAngleExample() {
            secondaryDev.actionControl(9);
        }

        /**
         * 设置车辆停车示例
         */
        public void setVehiParkExample() {
            secondaryDev.actionControl(10);
        }

        /**
         * 取消车辆设置示例
         */
        public void cancelSetVehiExample() {
            secondaryDev.actionControl(0);
        }

}
