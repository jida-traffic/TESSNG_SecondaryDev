import json
from typing import Dict, List
from DLLs.Tessng import PyCustomerNet, tessngIFace, Online, m2p, p2m


class MyNet(PyCustomerNet):
    def __init__(self):
        super(MyNet, self).__init__()
        self.iface = tessngIFace()
        self.netiface = self.iface.netInterface()
        self.simuiface = self.iface.simuInterface()

        self.signal_controller_id_mapping: Dict[int, int] = {}
        self.signal_phase_id_mapping: Dict[int, int] = {}

    def afterLoadNet(self):
        light_data = json.load(open("light_data.json", encoding="utf-8"))

        # 创建信号机、信控方案、信号相位
        for signal_controller_data in light_data["signalControllers"]:
            signal_controller_id: int = signal_controller_data["id"]
            signal_controller_name: str = signal_controller_data["name"]
            # 创建信号机
            signal_controller = self.netiface.createSignalController(signal_controller_name)
            self.signal_controller_id_mapping[signal_controller_id] = signal_controller.id()
            print(f"创建了信号机，id={signal_controller.id()}")
            for signal_plan_data in signal_controller_data["signalPlans"]:
                signal_plan_id: int = signal_plan_data["id"]
                signal_plan_name: str = signal_plan_data["name"]
                cycle_time: int = signal_plan_data["cycleTime"]
                start_time: int = signal_plan_data["startTime"]
                end_time: int = signal_plan_data["endTime"]
                offset: int = signal_plan_data["offset"]
                # 创建信控方案
                signal_plan = self.netiface.createSignalPlan(
                    signal_controller, signal_plan_name, cycle_time, offset, start_time, end_time
                )
                print(f"创建了信控方案，id={signal_plan.id()}")
                for signal_phase_data in signal_plan_data["signalPhases"]:
                    signal_phase_id: int = signal_phase_data["id"]
                    signal_phase_name: str = signal_phase_data["name"]
                    colors: List[str] = signal_phase_data["colors"]
                    durations: List[int] = signal_phase_data["durations"]
                    # 创建信号相位
                    color_intervals = [
                        Online.ColorInterval(color, duration)
                        for color, duration in zip(colors, durations)
                    ]
                    signal_phase = self.netiface.createSignalPlanSignalPhase(
                        signal_plan, signal_phase_name, color_intervals
                    )
                    self.signal_phase_id_mapping[signal_phase_id] = signal_phase.id()
                    print(f"创建了信号相位，id={signal_phase.id()}")
        print()

        # 创建信号灯头
        for signal_lamp in light_data["signalLamps"]:
            signal_lamp_name: str = signal_lamp["name"]
            link_ids: List[int] = signal_lamp["linkIds"]
            signal_controller_id: int = signal_lamp["signalControllerId"]
            signal_controller_tess_id: int = self.signal_controller_id_mapping[signal_controller_id]
            signal_controller = self.netiface.findSignalControllerById(signal_controller_tess_id)
            signal_phase_ids: List[int] = signal_lamp["signalPhaseIds"]
            signal_phase_tess_ids: List[int] = [
                self.signal_phase_id_mapping[signal_phase_id]
                for signal_phase_id in signal_phase_ids
            ]

            for link_id in link_ids:
                link = self.netiface.findLink(link_id)
                for lane in link.lanes():
                    lane_id = lane.id()
                    lane_length = p2m(lane.length())
                    dist = m2p(lane_length - 1)
                    signal_lamp = self.netiface.createTrafficSignalLamp(
                        signal_controller, signal_lamp_name, lane_id, -1, dist
                    )
                    print(f"创建了信号灯头，id={signal_lamp.id()}")
                    for signal_phase_id in signal_phase_tess_ids:
                        self.netiface.addSignalPhaseToLamp(signal_phase_id, signal_lamp)
