import torch
import torchvision
from torch.utils.mobile_optimizer import optimize_for_mobile

model = torch.jit.load("model/best.pt")

traced_script_module = torch.jit.script(model)
optimized_traced_model = optimize_for_mobile(traced_script_module)
optimized_traced_model._save_for_lite_interpreter("model.pt")

print("model successfully exported")
