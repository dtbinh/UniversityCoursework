class Checker:
    def __init__(self,infix):
        self.infix = infix
    def check(self, string):
        x = self.infix in string
        return x
