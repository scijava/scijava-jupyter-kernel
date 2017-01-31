"""
/*
 * Copyright 2016 kay schluehr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
"""

import __builtin__
import sys

###############################################################################

class DisplayHook:    
    '''
    An instance of this class is used to become the value of sys.displayhook.    
    
    DisplayHook inspects an object intance and tries to detect a special _repr_*_
    method as they are defined by IPython. Additionally the DisplayHook instance 
    stores the MIME type assiociated with that method, when it could be successfully
    executed.
    '''
    mimetypemap = (
        ('_repr_html_', 'text/html'), 
        ('_repr_png_',  'image/png'), 
        ('_repr_svg_',  'image/svg+xml'), 
        ('_repr_jpeg_', 'image/jpeg'),                
        ('_repr_markdown_',   'text/markdown'),
        ('_repr_json_',       'application/json'),
        ('_repr_javascript_', 'application/javascript'), 
        ('_repr_latex_', 'text/latex'), 
        ('__repr__',     'text/plain')
    )

    def __init__(self):
        # default mimetype
        self.print_expr = []
        self.mimetype = 'text/plain'  

    def read(self):
        S = ''.join(self.print_expr)
        self.print_expr = []
        return S

    def __call__(self, o):
        self.mimetype = 'text/plain'  
        if o is None:
            return
        __builtin__._ = None        
        for (repr_name, mimetype) in self.mimetypemap:
            repr = getattr(o, repr_name, None)
            if repr:
                try:
                    S = repr()
                    self.print_expr.append(S)
                except TypeError:
                    self.print_expr.append(str(o))
                self.mimetype = mimetype
                break
        else:            
            self.print_expr.append(str(o))
        __builtin__._ = o
    

sys.displayhook = DisplayHook()
del DisplayHook
