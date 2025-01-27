/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * $Id$
 *
 * Include file to access public window features
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$
 */
#ifndef _LWJGL_WINDOW_H_INCLUDED_
	#define _LWJGL_WINDOW_H_INCLUDED_

	#define WIN32_LEAN_AND_MEAN
	#ifndef _WIN32_WINDOWS
		#define _WIN32_WINDOWS 0x0400
	#endif
	#ifndef WINVER
		#define WINVER 0x0400
	#endif
	#ifndef _WIN32_WINNT
		#define _WIN32_WINNT 0x0000
	#endif
	#ifndef _WIN32_IE
		#define _WIN32_IE 0x0300
	#endif

	#if defined(UNICODE) || defined(_UNICODE)
		#include <tchar.h>
		#ifndef UNICODE
			#define UNICODE
		#endif
		#ifndef _UNICODE
			#define _UNICODE
		#endif
	#endif

	//#define _UNICODE
	//#include <tchar.h>

	//#define UNICODE
	#include <windows.h>
	#include <jni.h>
	#include "common_tools.h"

	#ifdef _PRIVATE_WINDOW_H_
		#define WINDOW_H_API
	#else
		#define WINDOW_H_API extern
	#endif /* _PRIVATE_WINDOW_H_ */

#endif /* _LWJGL_WINDOW_H_INCLUDED_ */
